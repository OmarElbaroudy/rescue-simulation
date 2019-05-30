package view;

import controller.CommandCenter;
import model.disasters.*;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.*;
import simulation.Address;
import simulation.Rescuable;

import java.util.*;

public class helper {
    private HashMap<Rescuable,Integer> hm;
    private CommandCenter sos;
    private static final int INF = (int)(10e8);
    public helper(CommandCenter sos){
        this.sos=sos;
        hm=new HashMap<>();
        for (ResidentialBuilding b: sos.getEngine().getBuildings()){
                 hm.put(b,-1);
        }
        for (Citizen c:sos.getEngine().getCitizens()){
            hm.put(c,-1);
        }
    }

    public String getHelp() {
        updateBuildingsPriority();
        updateCitizensPriority();
        Rescuable r = null;
        int max = -INF;
        for (Map.Entry<Rescuable, Integer> entry : hm.entrySet()) {
            if (entry.getValue() > max) {
                r = entry.getKey();
                max = entry.getValue();
            }
        }
        if (max == -1) return "end current cycle!";
        else {
            String unit = getUnitName(getCorrespondingUnit(r));
            return "send the " + unit + " at location " + getCorrespondingUnit(r).getLocation().toString() +
                    " to the " + ((r instanceof ResidentialBuilding) ? "builing at location " : "citizen at location ") +
                    r.getLocation().toString();
        }
    }
    private int cyclesToDecease(Rescuable r){
        if (r instanceof ResidentialBuilding){
            ResidentialBuilding b= (ResidentialBuilding)r;
            if (b.getDisaster().isActive()){
                int dmgPrCycle=getInflictedDamage(b.getDisaster());
                int crntDmg=getCurrentDamage(b);
                crntDmg=100-crntDmg;
                return crntDmg/dmgPrCycle;
            }else return INF;
        }else{
            Citizen c=(Citizen)r;
            if (c.getDisaster().isActive()){
                int dmgPrCycle=getInflictedDamage(c.getDisaster());
                int crntDmg=getCurrentDamage(c);
                crntDmg=100-crntDmg;
                return crntDmg/dmgPrCycle;
            }else
                return INF;
        }
    }
    private  int cyclesToReach(Rescuable r, Unit u){
        int stpsPerCycle=u.getStepsPerCycle();
        int distanceToTarget=ManhattanDistance(r.getLocation(),u.getLocation());
        return distanceToTarget/stpsPerCycle;
    }

    public static int ManhattanDistance(Address a, Address b) {
        return Math.abs(a.getY()-b.getY())+Math.abs(a.getX()-b.getX());
    }
    private int damageUponArrival(Unit u,Rescuable r){
        int cyclesToReach=cyclesToReach(r,u);
        int damage=getCurrentDamage(r);
        damage+=(cyclesToReach*getInflictedDamage(r.getDisaster()));
        return damage;
    }
    private boolean isReachable(Rescuable r, Unit u){
        if (u==null) return false;
        int cyclesToReach=cyclesToReach(r,u);
        int cyclesToDecease=cyclesToDecease(r);
        return cyclesToReach < cyclesToDecease;
    }
    private void updateBuildingsPriority(){
        ArrayList<ResidentialBuilding> buildings=this.sos.getEngine().getBuildings();
        for (ResidentialBuilding b: buildings){
            Unit u=getCorrespondingUnit(b);
            if (!isReachable(b,u)){
                hm.replace(b,-1);
            }else{
                int dmg=damageUponArrival(u,b);
                int priority=(dmg>0 && dmg<100)?dmg:-1;
                if (priority!=-1){
                    priority=(b.getOccupants().size()>0)?priority*b.getOccupants().size():0;
                }
                if (u.getLocation().equals(b.getLocation()))
                    hm.replace(b,-1);
                else
                    hm.replace(b,priority);
            }
        }
    }
    private void updateCitizensPriority(){
        ArrayList<Citizen> citizens=this.sos.getEngine().getCitizens();
        for (Citizen c: citizens){
            Unit u=getCorrespondingUnit(c);
            if (!isReachable(c,u)){
                hm.replace(c,-1);
            }else{
                int dmg=damageUponArrival(u,c);
                int priority=(dmg>0 && dmg<100)?dmg:-1;
                hm.replace(c,priority);
                if (u.getLocation().equals(c.getLocation()))
                    hm.replace(c,-1);
                else
                    hm.replace(c,priority);
            }
        }
    }

    private int getInflictedDamage(Disaster D){
        if (D instanceof Collapse) //foundation Damage
            return 10;
        if (D instanceof Fire) //fireDamage
            return 10;
        if (D instanceof GasLeak) //gasLevel
            return 15;
        if (D instanceof Infection) //toxicity
           return  15;
        if (D instanceof Injury)
            return 10; //bloodloss
        else return -1;
    }
    private int getCurrentDamage(Rescuable r){
        Disaster d= r.getDisaster();
        if (d instanceof Injury || d instanceof Infection){
            Citizen c= (Citizen) r;
            if (d instanceof Injury)
                return c.getBloodLoss();
            else return c.getToxicity();
        }else{
            ResidentialBuilding b= (ResidentialBuilding) r;
            if (d instanceof Collapse)
                return b.getFoundationDamage();

            else if (d instanceof Fire)
                return b.getFireDamage();

            else return b.getGasLevel();
        }
    }
    private Unit getCorrespondingUnit(Rescuable r){ //gets the most suitable unit according to availabilty and nearest location
      ArrayList<Unit> arr=getSuitableUnits(r);
      if (arr.isEmpty()) return null;
      else{
          PriorityQueue<pair> pq=new PriorityQueue<>();
          for (Unit u:arr){
              pq.add(new pair(u,r));
          }
          return pq.poll().u;
      }

    }
    private ArrayList<Unit> getSuitableUnits(Rescuable r){ //returns arrayList of Idle and responding units only
        Disaster d=r.getDisaster();
        ArrayList<Unit> arr= new ArrayList<>();
        ArrayList<Unit> units=this.sos.getEngine().getEmergencyUnits();
        if (d instanceof Collapse){
            for (Unit u:units){
                if (u instanceof Evacuator && isReachable(r,u) && u.getState()!= UnitState.RESPONDING)
                    arr.add(u);
            }
        }else if (d instanceof Fire){
            for (Unit u:units){
                if (u instanceof FireTruck && isReachable(r,u) && u.getState()!= UnitState.RESPONDING)
                    arr.add(u);
            }
        }else if (d instanceof GasLeak){
            for (Unit u:units){
                if (u instanceof GasControlUnit && isReachable(r,u) && u.getState()!= UnitState.RESPONDING)
                    arr.add(u);
            }
        }else if (d instanceof Infection){
            for (Unit u:units){
                if (u instanceof DiseaseControlUnit && isReachable(r,u) && u.getState()!= UnitState.RESPONDING)
                    arr.add(u);
            }
        }else if (d instanceof Injury){
            for (Unit u:units){
                if (u instanceof Ambulance && isReachable(r,u) && u.getState()!= UnitState.RESPONDING)
                    arr.add(u);
            }
        }
        return arr;
    }
    private String getUnitName(Unit u){
        if (u instanceof Ambulance) return "Ambulance ";
        if (u instanceof Evacuator) return "Evacuator";
        if (u instanceof GasControlUnit) return "Gas Control Unit";
        if (u instanceof FireTruck) return "Fire Truck";
        else return "Disease Control Unit";
    }

    static class pair implements Comparable<pair>{
        Unit u;
        Rescuable r;
        int distance;
        int state;
        public pair(Unit u,Rescuable r){
            this.u=u;
            this.r=r;
            distance=helper.ManhattanDistance(u.getLocation(),r.getLocation());
            switch (u.getState()){
                case IDLE:
                    state=1;
                    break;
                case TREATING:
                    state=2;
                    break;
            }
        }

        @Override
        public int compareTo(pair o) {
            return (distance!=o.distance)?distance-o.distance:state-o.state;
        }
    }
}
