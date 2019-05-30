package model.units;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;

public class Evacuator extends PoliceUnit {

	public Evacuator(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener, int maxCapacity) {
		super(unitID, location, stepsPerCycle, worldListener, maxCapacity);

	}

	@Override
	public void treat() {
		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0
				|| target.getOccupants().size() == 0) {
			jobsDone();
			return;
		}

		for (int i = 0; getPassengers().size() != getMaxCapacity()
				&& i < target.getOccupants().size(); i++) {
			getPassengers().add(target.getOccupants().remove(i));
			i--;
		}

		setDistanceToBase(target.getLocation().getX()
				+ target.getLocation().getY());

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
