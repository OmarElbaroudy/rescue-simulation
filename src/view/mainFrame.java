package view;

import controller.CommandCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainFrame  extends JFrame implements ActionListener {
    private CommandCenter sos;
    private JButton nxtCycle;
    private JButton hint;
    private infoPanel infoPanel;
    private rescuePanel rescuePanel;
    private unitsPanel unitsPanel;
    private int currcycle;
    private int casualties;
    private helper help;

    public mainFrame(CommandCenter sos){
        super("Rescue Simulation");
        this.sos=sos;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1400,1000);
        setLayout(new BorderLayout());

        nxtCycle=new JButton("End Current Cycle");
        hint=new JButton("Give Me A Hint!");
        infoPanel=new infoPanel(sos);
        rescuePanel=new rescuePanel(sos);
        unitsPanel=new unitsPanel(sos);
        help=new helper(sos);
        currcycle=0;
        casualties=0;

        add(hint,BorderLayout.PAGE_START);
        add(nxtCycle,BorderLayout.PAGE_END);
        add(infoPanel,BorderLayout.WEST);
        add(rescuePanel,BorderLayout.CENTER);
        add(unitsPanel,BorderLayout.EAST);

        nxtCycle.addActionListener(this);
        hint.addActionListener(this);

        rescuePanel.setInfoPanel(infoPanel);
        rescuePanel.setUnitsPanel(unitsPanel);
        unitsPanel.setRescuePanel(rescuePanel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            JButton clicked=(JButton)e.getSource();
            if (clicked==nxtCycle){
                sos.getEngine().nextCycle();
                rescuePanel.updateButtonState();
                unitsPanel.updateButtonState();
                casualties=sos.getEngine().calculateCasualties();
                currcycle++;
                if (sos.getEngine().checkGameOver()){
                    JOptionPane.showMessageDialog(null,"Game Over!\n"+
                            "final score: "+casualties+" casualties");
                    System.exit(0);
                }
                infoPanel.clear();
                infoPanel.updateDisasterLog();
                unitsPanel.updateGameLog(currcycle);
                infoPanel.printCurrentCycle(currcycle,casualties);
            }else if(clicked == hint){
                String s=help.getHelp();
                JOptionPane.showMessageDialog(null,s);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
