package io.openems.edge.ess.generic.symmetric.statemachine;

import java.time.Duration;
import java.time.Instant;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.statemachine.StateHandler;

public class ErrorHandling extends StateHandler<State, Context> {

	private Instant entryAt = Instant.MIN;

	@Override
	protected void onEntry(Context context) throws OpenemsNamedException {
		this.entryAt = Instant.now();

		// Try to stop systems
		context.battery.setStartStop(StartStop.STOP);
		context.batteryInverter.setStartStop(StartStop.STOP);
	}

	@Override
	protected void onExit(Context context) throws OpenemsNamedException {
		context.component._setMaxBatteryStartAttempts(false);
		context.component._setMaxBatteryInverterStopAttempts(false);
	}

	@Override
	public State getNextState(Context context) {
		System.out.println("Stuck in ERROR_HANDLING: " + context.component.getState().listStates());

		if (Duration.between(this.entryAt, Instant.now()).getSeconds() > 120) {
			// Try again
			return State.UNDEFINED;
		}

		return State.ERROR_HANDLING;
	}

}
