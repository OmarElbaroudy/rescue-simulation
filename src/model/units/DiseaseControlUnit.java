package model.units;

import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.events.WorldListener;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Address;
import simulation.Rescuable;

public class DiseaseControlUnit extends MedicalUnit {

	public DiseaseControlUnit(String unitID, Address location, int stepsPerCycle, WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
	}

	@Override
	public void treat() {
		getTarget().getDisaster().setActive(false);
		Citizen target = (Citizen) getTarget();
		if (target.getHp() == 0) {
			jobsDone();
			return;
		} else if (target.getToxicity() > 0) {
			target.setToxicity(target.getToxicity() - getTreatmentAmount());
			if (target.getToxicity() == 0)
				target.setState(CitizenState.RESCUED);
		}

		else if (target.getToxicity() == 0)
			heal();

	}

	public void respond(Rescuable r) throws Exception {
		if (!(r instanceof Citizen))
			throw new IncompatibleTargetException(this, r, "The target should be a Citizen");
		if(this.canTreat(r)==false)
			throw new CannotTreatException(this,r,"the Target is already safe");
		if(((Citizen)r).getState()== CitizenState.DECEASED)
			throw new CitizenAlreadyDeadException(r.getDisaster(),"the Target is already dead");
		if (getTarget() != null && ((Citizen) getTarget()).getToxicity() > 0 && getState() == UnitState.TREATING)
			reactivateDisaster();
		finishRespond(r);
	}

}
