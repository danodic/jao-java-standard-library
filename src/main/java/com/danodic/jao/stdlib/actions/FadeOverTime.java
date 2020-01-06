package com.danodic.jao.stdlib.stdlib.actions;

import com.danodic.jao.action.Action;
import com.danodic.jao.action.IAction;
import com.danodic.jao.core.JaoLayer;
import com.danodic.jao.model.ActionModel;
import com.danodic.jao.parser.expressions.TimeExpressionParser;

@Action(name = "FadeOverTime", library = "jao.standards")
public class FadeOverTime implements IAction {

	// The start and end point of the opacity for pulse
	private float startOpacity;
	private float endOpacity;
	private float currentOpacity;
	private Long duration;
	private Long elapsed;
	private Long startTime;
	private Long endTime;

	// Define if the event is done
	private boolean done;

	public FadeOverTime() {
		this(0f, 1f, 1000L);
	}

	public FadeOverTime(float startOpacity, float endOpacity, Long duration) {

		// Initialize stuff
		done = false;

		// Define standard opacity values
		this.startOpacity = startOpacity;
		this.endOpacity = endOpacity;
		this.duration = duration;
		this.startTime = null;
		this.endTime = null;
		this.currentOpacity = startOpacity;

		// Initialize values
		reset();
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public void run(JaoLayer object) {

		// Reset the start and end time
		if (startTime == null || endTime == null) {
			startTime = object.getElapsed();
			endTime = startTime + duration;
		}

		// Get the elapsed time
		elapsed = object.getElapsed() - startTime;

		// Increment/decrement step
		currentOpacity = startOpacity + ((elapsed.floatValue() / duration.floatValue()) * (endOpacity - startOpacity));

		// Set the object current opacity
		object.getParameters().put("opacity", currentOpacity);

		// Check if we reached the target
		if (Long.compare(elapsed, duration) >= 0) {
			object.getParameters().put("opacity", endOpacity);
			done = true;
			return;
		}
	}

	@Override
	public void reset() {
		startTime = null;
		endTime = null;
		this.currentOpacity = startOpacity;
		done = false;
	}

	@Override
	public void loadModel(ActionModel model) {
		if (model.getAttributes().containsKey("start_opacity"))
			startOpacity = Float.parseFloat(model.getAttributes().get("start_opacity"));

		if (model.getAttributes().containsKey("end_opacity"))
			endOpacity = Float.parseFloat(model.getAttributes().get("end_opacity"));

		if (model.getAttributes().containsKey("duration"))
			duration = TimeExpressionParser.parseExpression(model.getAttributes().get("duration"));
	}

	@Override
	public void setLoop(boolean loop) {
	}

	@Override
	public boolean isLoop() {
		return false;
	}

}