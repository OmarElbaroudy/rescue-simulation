package view;

import controller.CommandCenter;
import model.disasters.*;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;

public class unitsPanel extends JPanel implements ActionListener {
    private CommandCenter sos;
    private rescuePanel rescuePanel;
    private GridBagConstraints gc;
    private JToggleButton evacuator;
    private JToggleButton ambulance;
    private JToggleButton fireTruck;
    private JToggleButton disCntrlUnt;
    private JToggleButton gasCntrlUnt;
    private JLabel unitLabel;
    private JTextArea unitArea;
    private JLabel gameLog;
    private JTextArea gameArea;
    private StringBuilder casualties;
    private Object[] barr,carr;

    public unitsPanel(CommandCenter sos){
        this.sos=sos;
        this.evacuator=new JToggleButton("Evacuator");
        this.ambulance=new JToggleButton("Ambulance");
        this.fireTruck=new JToggleButton("Fire Truck");
        this.disCntrlUnt=new JToggleButton("Disease Control Unit");
        this.gasCntrlUnt=new JToggleButton("Gas Control Unit");
        this.unitArea=new JTextArea(25,25);
        this.unitLabel=new JLabel("Unit Stats: ");
        this.gameLog=new JLabel("Game Log: ");
        this.gameArea=new JTextArea(25,25);


        unitArea.setEditable(false);
        gameArea.setEditable(false);
        evacuator.addActionListener(this);
        ambulance.addActionListener(this);
        disCntrlUnt.addActionListener(this);
        gasCntrlUnt.addActionListener(this);
        fireTruck.addActionListener(this);
        Dimension dim=getPreferredSize();
        dim.width=400;
        setPreferredSize(dim);

        unitArea.setFont(new Font(Font.SERIF,Font.BOLD,12));
        gameArea.setFont(new Font(Font.SERIF,Font.BOLD,11));


        casualties=new StringBuilder();
        barr=this.sos.getEngine().getBuildings().toArray().clone();
        carr=this.sos.getEngine().getCitizens().toArray().clone();

        Border innerBorder=BorderFactory.createTitledBorder("Units Panel");
        ((TitledBorder) innerBorder).setTitleJustification(TitledBorder.RIGHT);
        Border outerBorder=BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder,innerBorder));
        setLayout(new GridBagLayout());

        /////////first Row////////////////////////////////////////////////
        gc=setgc(0,0,1,1);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.CENTER;
        add(evacuator,gc);
        //////////second Row///////////////////////////////////////////////
        gc=setgc(0,1,1,1);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.CENTER;
        add(ambulance,gc);
        /////////3rd Row//////////////////////////////////////////////////
        gc=setgc(0,2,1,1);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.CENTER;
        add(fireTruck,gc);
        ////////4th Row////////////////////////////////////////////////////
        gc=setgc(0,3,1,1);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.CENTER;
        add(disCntrlUnt,gc);
        ///////5th Row//////////////////////////////////////////////////////
        gc=setgc(0,4,1,1);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.CENTER;
        add(gasCntrlUnt,gc);
        ///////6th Row////////////////////////////////////////////////////
        gc=setgc(0,5,1,1);
        gc.anchor=GridBagConstraints.LINE_START;
        add(unitLabel,gc);
        ///////7th Row//////////////////////////////////////////////////////
        gc=setgc(0,6,1,50);
        gc.fill=GridBagConstraints.BOTH;
        gc.anchor=GridBagConstraints.CENTER;
        add(new JScrollPane(unitArea),gc);
        //////8th Row//////////////////////////////////////////////////////
        gc=setgc(0,7,1,1);
        gc.fill=GridBagConstraints.BOTH;
        gc.anchor=GridBagConstraints.LINE_START;
        add(gameLog,gc);
        //////9th Row//////////////////////////////////////////////////////
        gc=setgc(0,8,1,90);
        gc.fill=GridBagConstraints.BOTH;
        gc.anchor=GridBagConstraints.CENTER;
        add(new JScrollPane(gameArea),gc);

    }
    public void setRescuePanel(view.rescuePanel rescuePanel) {
        this.rescuePanel = rescuePanel;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        JToggleButton clicked= (JToggleButton) e.getSource();
        Unit u=null;
        if (clicked==evacuator){
            ambulance.setSelected(false);
            disCntrlUnt.setSelected(false);
            gasCntrlUnt.setSelected(false);
            fireTruck.setSelected(false);
            u=getCorrespondingUnit(evacuator);
            unitArea.append(UnitInfo(u));
        }else if (clicked==ambulance){
            evacuator.setSelected(false);
            disCntrlUnt.setSelected(false);
            gasCntrlUnt.setSelected(false);
            fireTruck.setSelected(false);
            u=getCorrespondingUnit(ambulance);
            unitArea.append(UnitInfo(u));
        }else if (clicked==fireTruck){
            ambulance.setSelected(false);
            disCntrlUnt.setSelected(false);
            gasCntrlUnt.setSelected(false);
            evacuator.setSelected(false);
            u=getCorrespondingUnit(fireTruck);
            unitArea.append(UnitInfo(u));
        }else if (clicked==disCntrlUnt){
            ambulance.setSelected(false);
            evacuator.setSelected(false);
            gasCntrlUnt.setSelected(false);
            fireTruck.setSelected(false);
            u=getCorrespondingUnit(disCntrlUnt);
            unitArea.append(UnitInfo(u));
        }else{
            ambulance.setSelected(false);
            disCntrlUnt.setSelected(false);
            evacuator.setSelected(false);
            fireTruck.setSelected(false);
            u=getCorrespondingUnit(gasCntrlUnt);
            unitArea.append(UnitInfo(u));
        }
    }

    public String UnitInfo(Unit u){
        unitArea.setText(null);
        return "unit State: "+u.getState()+"\n"+
                "unit Target at location: "+((u.getTarget()==null)?"N/A":u.getTarget().getLocation())+"\n"+
                "unit steps per cycle: "+u.getStepsPerCycle()+"\n"+
                "unit current Location: "+u.getLocation().toString()+"\n"+
                ((u instanceof Evacuator)?evacInfo(u):"");
    }

    private String evacInfo(Unit u) {
        Evacuator e=((Evacuator) u);
        StringBuilder sb=new StringBuilder();
        sb.append("Evacutor carrying "+e.getPassengers().size()+" passengers\n");
        int cnt=1;
        for (Citizen c:e.getPassengers()){
            String s=getCitizenInfo(c);
            sb.append("Citizen "+(cnt++)+":\n"+s);
        }
        return sb.toString();
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
                "Disaster Affecting Citizen: "+(c.getDisaster()==null?"N/A":c.getDisaster())+"\n");
    }

    public void updateButtonState() {
        ArrayList<Unit> arr=sos.getEngine().getEmergencyUnits();
        JButton[][] btns=rescuePanel.getBtnArray();
        for (Unit u:arr){
            int x=0,y=0;
            if (u.getTarget()!=null){
                x=u.getTarget().getLocation().getX();
                y=u.getTarget().getLocation().getY();
            }
            if (u instanceof Evacuator){
                UnitState state=u.getState();
                switch (state){
                    case TREATING:
                        evacuator.setBackground(Color.green);
                        btns[x][y].setBackground(Color.green);
                        break;
                    case RESPONDING:
                        evacuator.setBackground(Color.yellow);
                        btns[x][y].setBackground(Color.yellow);
                        break;
                    case IDLE:
                        evacuator.setBackground(null);

                }
            }else if (u instanceof Ambulance){
                UnitState state=u.getState();
                switch (state){
                    case TREATING:
                        ambulance.setBackground(Color.green);
                        btns[x][y].setBackground(Color.green);
                        break;
                    case RESPONDING:
                        ambulance.setBackground(Color.yellow);
                        btns[x][y].setBackground(Color.yellow);
                        break;
                    case IDLE:
                        ambulance.setBackground(null);
                }
            }else if (u instanceof GasControlUnit){
                UnitState state=u.getState();
                switch (state){
                    case TREATING:
                        gasCntrlUnt.setBackground(Color.green);
                        btns[x][y].setBackground(Color.green);
                        break;
                    case RESPONDING:
                        gasCntrlUnt.setBackground(Color.yellow);
                        btns[x][y].setBackground(Color.yellow);
                        break;
                    case IDLE:
                        gasCntrlUnt.setBackground(null);

                }
            }else if (u instanceof DiseaseControlUnit){
                UnitState state=u.getState();
                switch (state){
                    case TREATING:
                        disCntrlUnt.setBackground(Color.green);
                        btns[x][y].setBackground(Color.green);
                        break;
                    case RESPONDING:
                        disCntrlUnt.setBackground(Color.yellow);
                        btns[x][y].setBackground(Color.yellow);
                        break;
                    case IDLE:
                        disCntrlUnt.setBackground(null);

                }
            }else{
                UnitState state=u.getState();
                switch (state){
                    case TREATING:
                        fireTruck.setBackground(Color.green);
                        btns[x][y].setBackground(Color.green);
                        break;
                    case RESPONDING:
                        fireTruck.setBackground(Color.yellow);
                        btns[x][y].setBackground(Color.yellow);
                        break;
                    case IDLE:
                        fireTruck.setBackground(null);

                }
            }
        }
    }
    public JToggleButton getSelectedBtn(){
        if (evacuator.isSelected()) return evacuator;
        if (ambulance.isSelected()) return ambulance;
        if (disCntrlUnt.isSelected()) return disCntrlUnt;
        if (gasCntrlUnt.isSelected()) return gasCntrlUnt;
        if (fireTruck.isSelected()) return fireTruck;
        return null;
    }
    public Unit getCorrespondingUnit(JToggleButton btn){
        if (btn==evacuator){
            for (Unit unit:this.sos.getEngine().getEmergencyUnits())
                if (unit instanceof Evacuator){
                    return unit;
                }
        }
        if (btn==ambulance){
            for (Unit unit:this.sos.getEngine().getEmergencyUnits())
                if (unit instanceof Ambulance){
                    return unit;
                }
        }
        if (btn==fireTruck) {
            for (Unit unit : this.sos.getEngine().getEmergencyUnits())
                if (unit instanceof FireTruck) {
                    return unit;
                }
        }
        if (btn==gasCntrlUnt){
            for (Unit unit : this.sos.getEngine().getEmergencyUnits())
                if (unit instanceof GasControlUnit) {
                    return unit;
                }
        }
        for (Unit unit : this.sos.getEngine().getEmergencyUnits())
            if (unit instanceof DiseaseControlUnit) {
                return unit;
            }
        return null;
    }
    public void updateGameLog(int cycle){
        StringBuilder sb=new StringBuilder();
        ArrayList<Disaster> arr=this.sos.getEngine().getExecutedDisasters();
        for (Disaster d:arr){
            if (d instanceof Collapse){
                sb.append("Disaster of type Collapse occurred to building at location:"+
                        d.getTarget().getLocation().toString()+
                        "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
            }else if (d instanceof Fire){
                sb.append("Disaster of type Fire occurred to building at location:"+
                        d.getTarget().getLocation().toString()+
                        "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
            }else if (d instanceof GasLeak){
                sb.append("Disaster of type Gas leak occurred to building at location:"+
                        d.getTarget().getLocation().toString()+
                        "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
            }else if (d instanceof Infection){
                sb.append("Disaster of type Infection occurred to citizen: "+
                        (((Citizen)d.getTarget()).getName())+
                        "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
            }else{
                sb.append("Disaster of type Injury to citizen: "+
                        (((Citizen)d.getTarget()).getName())+
                        "\n"+"disaster struck in cycle: "+d.getStartCycle()+"\n");
            }
        }
        gameArea.setText(null);
        gameArea.setText(sb.toString());
        gameArea.append("_____________________________\n");
        gameArea.append("Casualties so far:\n");
        gameArea.append(updateCasualtiesLog(cycle));
    }
    public String updateCasualtiesLog(int cycle){
        for (int i=0; i<barr.length; i++) {
            if (barr[i]!= null && ((ResidentialBuilding)barr[i]).getStructuralIntegrity() == 0) {
                casualties.append("Building at location " +
                        ((ResidentialBuilding)barr[i]).getLocation().toString() +
                        " has fallen in cycle " + cycle + "\n");
                barr[i]=null;
            }
        }
        for (int i=0; i<carr.length; i++){
            if (carr[i]!=null && ((Citizen)carr[i]).getState()==CitizenState.DECEASED){
                casualties.append("Citizen "+((Citizen)carr[i]).getName()+" has died in cycle "+
                        cycle+"\n");
                carr[i]=null;
            }
        }
       return casualties.toString();
    }
}

