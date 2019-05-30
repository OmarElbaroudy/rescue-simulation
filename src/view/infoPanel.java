package view;

import controller.CommandCenter;
import model.disasters.*;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.*;
import simulation.Rescuable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class infoPanel extends JPanel {
    private CommandCenter sos;
    private JTextArea buildingArea;
    private JTextArea citizenArea;
    private JLabel cycleLabel;
    private JLabel buildingLabel;
    private JLabel citizenLabel;
    private JTextField cycleField;
    private JLabel disasterLabel;
    private JTextArea disasterArea;
    private JLabel unitLabel;
    private JTextArea unitArea;
    private GridBagConstraints gc;

    public infoPanel(CommandCenter sos){
        this.sos=sos;
        Dimension dim=getPreferredSize();
        dim.width=400;
        setPreferredSize(dim);
//        setBackground(new Color(178,207,229));
        this.cycleField=new JTextField(25);
        this.buildingArea=new JTextArea(25,25);
        this.citizenArea=new JTextArea(25,25);
        this.disasterArea=new JTextArea(25,25);
        this.cycleLabel=new JLabel("Current cycle stats: ");
        this.buildingLabel=new JLabel("Building: ");
        this.citizenLabel=new JLabel("Citizen: ");
        this.disasterLabel=new JLabel("Disasters log: ");
        this.unitLabel=new JLabel("Unit: ");
        this.unitArea=new JTextArea(25,25);

        unitArea.setEditable(false);
        cycleField.setEditable(false);
        citizenArea.setEditable(false);
        disasterArea.setEditable(false);
        buildingArea.setEditable(false);

        unitArea.setFont(new Font(Font.SERIF,Font.BOLD,12));
        buildingArea.setFont(new Font(Font.SERIF,Font.BOLD,12));
        disasterArea.setFont(new Font(Font.SERIF,Font.BOLD,12));
        cycleField.setFont(new Font(Font.SERIF,Font.BOLD,12));
        citizenArea.setFont(new Font(Font.SERIF,Font.BOLD,12));

        setLayout(new GridBagLayout());
        Border innerBorder=BorderFactory.createTitledBorder("Info Panel");
        Border outerBorder=BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder,innerBorder));


        //////////////////first Row //////////////////////////////////////////
        gc=setgc(0,0,1,0.1);
        gc.anchor=GridBagConstraints.LINE_START;
        add(cycleLabel,gc);


        gc=setgc(0,1,1,0.1);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.fill=GridBagConstraints.HORIZONTAL;
        add(cycleField,gc);

        ////////////////////////2nd Row///////////////////////////////////////
        gc=setgc(0,2,1,0.06);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        add(citizenLabel,gc);


        gc=setgc(0,3,100,1);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(citizenArea),gc);

        ////////////////////3rd Row////////////////////////////////////////////////
        gc=setgc(0,4,1,0.1);
        gc.anchor=GridBagConstraints.LINE_START;
        add(buildingLabel,gc);

        gc=setgc(0,5,100,1);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(buildingArea),gc);
        //////////////////////4th Row ///////////////////////////////////////////////
        gc=setgc(0,6,1,0.1);
        gc.anchor=GridBagConstraints.LINE_START;
        add(unitLabel,gc);

        gc=setgc(0,7,100,1);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(unitArea),gc);

        //////////////////////5th Row ///////////////////////////////////////////////
        gc=setgc(0,8,1,0.1);
        gc.anchor=GridBagConstraints.LINE_START;
        add(disasterLabel,gc);

        gc=setgc(0,9,100,1);
        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.fill=GridBagConstraints.BOTH;
        add(new JScrollPane(disasterArea),gc);

    }
    public void printCurrentCycle(int cycle, int casualties){
        this.cycleField.setText(null);
        this.cycleField.setText("cycle "+cycle+", "+casualties+" casualties so far!");
    }
    public GridBagConstraints setgc(int gx, int gy, double wx, double wy){
        GridBagConstraints gc=new GridBagConstraints();
        gc.gridx=gx;
        gc.gridy=gy;
        gc.weightx=wx;
        gc.weighty=wy;
        gc.fill=GridBagConstraints.NONE;
        return gc;
    }
    public void printCitizen(Citizen c){
        String info=getCitizenInfo(c);
        this.citizenArea.setText(null);
        this.buildingArea.setText(null);
        this.citizenArea.append(info);
    }
    private String getCitizenInfo(Citizen c){
        return ("Citizen name: "+c.getName()+"\n"+
                "Citizen National ID: "+c.getNationalID()+"\n"+
                "Citizen Age: "+c.getAge()+"\n"+
                "Citizen Location: "+c.getLocation().toString()+"\n"+
                "Citizen State: "+c.getState()+"\n"+
                "Citizen HP: "+c.getHp()+"\n"+
                "Citizen BloodLoss: "+c.getBloodLoss()+"\n"+
                "Citizen Toxicity: "+c.getToxicity()+"\n"+
                "Disaster Affecting Citizen: "+getDisasterType(c)+"\n");
    }
    public void printBuilding(ResidentialBuilding b){
        this.buildingArea.setText(null);
        this.citizenArea.setText(null);
        String buildingInfo= getBuildingInfo(b);
        String occupantsInfo=getOccupantsInfo(b);
        this.buildingArea.append(buildingInfo);
        this.citizenArea.append(occupantsInfo);
    }
    public String getBuildingInfo(ResidentialBuilding b){
        return ("Building Location: "+b.getLocation().toString()+"\n"+
                "Building Structural Integrity: "+b.getStructuralIntegrity()+"\n"+
                "Building Fire Damage: "+b.getFireDamage()+"\n"+
                "Building Gas Level: "+b.getGasLevel()+"\n"+
                "Building Foundation Damage: "+b.getFoundationDamage()+"\n"+
                "Number Of Occupants: "+b.getOccupants().size()+"\n"+
                "Disaster affecting Building: "+getDisasterType(b)+"\n");
    }

    private String getOccupantsInfo(ResidentialBuilding b) {
        StringBuilder sb=new StringBuilder();
        for (int i=0; i<b.getOccupants().size(); i++){
            Citizen c=b.getOccupants().get(i);
            sb.append("Citizen "+(i+1)+":\n"+getCitizenInfo(c)+"\n");
        }
        return sb.toString();
    }
    public void clear(){
        buildingArea.setText(null);
        citizenArea.setText(null);
        disasterArea.setText(null);
        unitArea.setText(null);
    }

    public void updateDisasterLog() {
        StringBuilder sb=new StringBuilder();
        ArrayList<Disaster> arr=this.sos.getEngine().getExecutedDisasters();
        int c=1;
        for (Disaster d:arr){
            if (d.isActive()){
                if (d instanceof Collapse){
                    sb.append("Disaster "+(c++)+":\n"+
                            "Collapse to building at location: "+d.getTarget().getLocation().toString()+
                            "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
                }else if (d instanceof Fire){
                    sb.append("Disaster "+(c++)+":\n"+
                            "Fire to building at location: "+d.getTarget().getLocation().toString()+
                            "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
                }else if (d instanceof GasLeak){
                    sb.append("Disaster "+(c++)+":\n"+
                            "Gas leak to building at location: "+d.getTarget().getLocation().toString()+
                            "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
                }else if (d instanceof Infection){
                    sb.append("Disaster "+(c++)+":\n"+
                            "Infection to citizen: "+(((Citizen)d.getTarget()).getName())+
                            "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
                }else{
                    sb.append("Disaster "+(c++)+":\n"+
                            "Injury to citizen: "+(((Citizen)d.getTarget()).getName())+
                            "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
                }
            }
        }
        disasterArea.setText(null);
        disasterArea.setText(sb.toString());
    }
    public String getDisasterType(Rescuable r){
        Disaster d=r.getDisaster();
        if (d!=null && d.isActive()) {
            if (d instanceof GasLeak) return "Gas Leak";
            if (d instanceof Infection) return "Infection";
            if (d instanceof Injury) return "Injury";
            if (d instanceof Collapse) return "Collapse";
            if (d instanceof Fire) return "Fire";
        }
        return "N/A";
    }

    public void updateUnits(int x, int y) {
        this.unitArea.setText(null);
        StringBuilder sb=new StringBuilder();
        ArrayList<Unit> arr=this.sos.getEngine().getEmergencyUnits();
        for (Unit u: arr){
            if (u.getLocation().getX()==x && u.getLocation().getY()==y){
                String s=getCorrespondingUnit(u);
                sb.append(s+" is present in this location ("+x+","+y+").\n");
            }
        }
        unitArea.append(sb.toString());
    }

    private String getCorrespondingUnit(Unit u) {
        if (u instanceof Evacuator) return "Evacuator";
        if (u instanceof Ambulance) return "Ambulance";
        if (u instanceof DiseaseControlUnit) return "Disease Control Unit";
        if (u instanceof GasControlUnit) return "Gas Control Unit";
        else return "Fire Truck";
    }
}
