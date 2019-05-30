package model.units;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import simulation.Address;
import simulation.Rescuable;

public class GasControlUnit extends FireUnit {

	public GasControlUnit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
	}

	public void treat() {
		getTarget().getDisaster().setActive(false);

		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0) {
			jobsDone();
			return;
		} else if (target.getGasLevel() > 0) 
			target.setGasLevel(target.getGasLevel() - 10);

		if (target.getGasLevel() == 0)
			jobsDone();

	}
	public void respond(Rescuable r) throws Exception{
		if (!(r instanceof ResidentialBuilding))
			throw new IncompatibleTargetException(this, r, "The target should be a ResidentialBuilding");
		if(this.canTreat(r)==false)
			throw new CannotTreatException(this,r,"the Target is already safe");
		if(((ResidentialBuilding)r).getStructuralIntegrity()==0)
			throw new BuildingAlreadyCollapsedException(r.getDisaster(),"the Building is already Collapsed");
		super.respond(r);
	}
}
