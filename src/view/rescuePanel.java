package view;

import controller.CommandCenter;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Unit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class rescuePanel extends JPanel implements ActionListener {
	private CommandCenter sos;
	private JButton[][] btnArray;
	private infoPanel infoPanel;
	private unitsPanel unitsPanel;

	public rescuePanel(CommandCenter sos) {
		this.sos = sos;
		this.btnArray = new JButton[10][10];
		Dimension dim = getPreferredSize();
		dim.width = 600;
		setPreferredSize(dim);

		Border innerBorder = BorderFactory.createTitledBorder("Rescue Panel");
		((TitledBorder) innerBorder).setTitleJustification(TitledBorder.CENTER);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		setLayout(new GridLayout(10, 10, 5, 5));
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				JButton b = new JButton();
				b.setBackground(Color.lightGray);
				try {
					if (i == j && i == 0) {
						Image img = ImageIO.read(getClass().getResource("base.png"));
						b.setIcon(new ImageIcon(img));
						b.setBackground(null);
					} else if (buildingPresent(i, j)) {
						Image img = ImageIO.read(getClass().getResource("build.png"));
						b.setIcon(new ImageIcon(img));
						b.setBackground(null);
					} else if (citizenPresent(i, j)) {
						Image img = ImageIO.read(getClass().getResource("man.png"));
						b.setIcon(new ImageIcon(img));
						b.setBackground(null);
					}

				} catch (Exception ex) {
					System.out.println("not found");
				}
				btnArray[i][j] = b;
				add(b, i, j);
			}
		}
		for (int i = 0; i < 10; i++) {
			for (JButton btn : btnArray[i]) {
				btn.addActionListener(this);
			}
		}
	}

	public JButton[][] getBtnArray() {
		return btnArray;
	}

	public void setInfoPanel(view.infoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	private boolean citizenPresent(int x, int y) {
		ArrayList<Citizen> arr = this.sos.getEngine().getCitizens();
		for (Citizen c : arr) {
			if (c.getLocation().getX() == x && c.getLocation().getY() == y)
				return true;
		}
		return false;
	}

	private boolean buildingPresent(int x, int y) {
		ArrayList<ResidentialBuilding> arr = this.sos.getEngine().getBuildings();
		for (ResidentialBuilding b : arr) {
			if (b.getLocation().getX() == x && b.getLocation().getY() == y)
				return true;
		}
		return false;
	}
	private boolean unitPresent(int x, int y){
		ArrayList<Unit> arr=this.sos.getEngine().getEmergencyUnits();
		for (Unit u: arr){
			if (u.getLocation().getX()==x && u.getLocation().getY()==y){
				return true;
			}
		}
		return false;
	}

	private Citizen getCitizenByLocation(int x, int y) {
		ArrayList<Citizen> arr = this.sos.getEngine().getCitizens();
		for (Citizen c : arr) {
			if (c.getLocation().getX() == x && c.getLocation().getY() == y)
				return c;
		}
		return null;
	}

	private ResidentialBuilding getBuildingByLocation(int x, int y) {
		ArrayList<ResidentialBuilding> arr = this.sos.getEngine().getBuildings();
		for (ResidentialBuilding b : arr) {
			if (b.getLocation().getX() == x && b.getLocation().getY() == y)
				return b;
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();
		JButton button = null;
		int x = 0, y = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (clicked == btnArray[i][j]) {
					button = btnArray[i][j];
					x = i;
					y = j;
					break;
				}
			}
		}
		if (button != null) {
			if (buildingPresent(x, y)) {
				ResidentialBuilding b = getBuildingByLocation(x, y);
				if (unitsPanel.getSelectedBtn() != null) {
					JToggleButton btn = unitsPanel.getSelectedBtn();
					Unit u = unitsPanel.getCorrespondingUnit(btn);
					try {
						u.respond(b);
						btn.setSelected(false);
						updateButtonState();
						unitsPanel.updateButtonState();
					}catch (Exception ex) {
						btn.setSelected(false);
						JOptionPane.showMessageDialog(null, ex.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				infoPanel.printBuilding(b);
			} else if (citizenPresent(x, y)) {
				Citizen c = getCitizenByLocation(x, y);
				infoPanel.printCitizen(c);
				if (unitsPanel.getSelectedBtn() != null) {
					JToggleButton btn = unitsPanel.getSelectedBtn();
					Unit u = unitsPanel.getCorrespondingUnit(btn);
					try {
						u.respond(c);
						btn.setSelected(false);
						updateButtonState();
						unitsPanel.updateButtonState();
					} catch (Exception ex) {
						btn.setSelected(false);
						JOptionPane.showMessageDialog(null, ex.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			if (unitPresent(x,y)){
				infoPanel.updateUnits(x,y);
			}
		}
	}

	public void updateButtonState() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (buildingPresent(i, j)) {
					ResidentialBuilding b = getBuildingByLocation(i, j);
					if (b.getStructuralIntegrity() == 0) {
						btnArray[i][j].setBackground(Color.gray);
					} else if (b.getDisaster() != null && b.getDisaster().isActive()) {
						btnArray[i][j].setBackground(Color.red);
					} else
						btnArray[i][j].setBackground(null);
				} else if (citizenPresent(i, j)) {
					Citizen c = getCitizenByLocation(i, j);
					if (c.getHp() == 0) {
						btnArray[i][j].setBackground(Color.gray);
					} else if (c.getDisaster() != null && c.getDisaster().isActive()) {
						btnArray[i][j].setBackground(Color.red);
					} else
						btnArray[i][j].setBackground(null);
				}
			}
		}
	}

	public void setUnitsPanel(view.unitsPanel unitsPanel) {
		this.unitsPanel = unitsPanel;
	}

}
