/**
 * Everything in here is done on a polar grid. A
 * polar grid is a coordinate system in which
 * location is defined not by (x,y) but by
 * (r,theta). r is the distance from the origin;
 * theta is the angle of rotation from the origin
 * 
 * To convert from a cartesian coordinate to a
 * polar coordinate, use convertPoint()
 * 
 * To convert from a polar coordinate to a
 * cartesian coordinate, use pointAt()
 * 
 * To get just t, use getAngle()
 */

"use strict";

/**
 * This object represents an arc.
 * Properties:
 * Children: An array of the child nodes
 * Start: The start angle, in radiians
 * End: The end angle, in radians
 * Dist: The distance from the center
 * Thickness: The thickness of the Arc
 * Label: The text label that represents the arc. "" for no label.
 * 
 * 
 * render():void
 * 
 * Renders the arc
 * 
 * @param ctx The canvas context object, given by element.getContext("2d")
 * @param center The origin of the plot, cartesian coordinates
 * @param renderingRules The RenderingRules object that defines how the plot is rendered. See RenderingRules
 * 
 * 
 * contains():boolean
 * 
 * Determines if the arc contains a given point in polar form. Not providing ctx will
 * cause the function to ignore any text in collision calculation.
 * 
 * @param dist The distance from the center
 * @param theta The angle of the point
 * @param ctx The canvas context object, given by element.getContext("2d"). Optional.
 * @param font The font with size and type included. Used for calculating arc size. Optional.
 * 
 * 
 * clone():Arc
 * Not yet supported
 */
function Arc(startAngle, endAngle, startDistance, endDistance, label){
	this.children = new Array(); //Arcs OR Curves
	
	this.start = startAngle;
	this.end = endAngle;
	
	this.dist = startDistance;
	this.thickness = endDistance - startDistance;
	
	this.label = label;
	
	this.render = function(ctx, center, renderingRules){
		var thetaStart = (this.start + renderingRules.rotation) % 256;
		var thetaEnd = (this.end + renderingRules.rotation) % 256;
		/* arc */
		ctx.save();
		ctx.fillStyle = renderingRules.arcFill;
		
		ctx.beginPath();
		ctx.arc(center.x, center.y, (this.dist + this.thickness) * renderingRules.scale, thetaStart, thetaEnd, false);
		ctx.arc(center.x, center.y, this.dist * renderingRules.scale, thetaEnd, thetaStart, true);
		ctx.closePath();
		ctx.fill();
		ctx.stroke();
	
		ctx.restore();
		
		/* text */
		if(label != "" && label != null){
			var theta = ((thetaStart + thetaEnd) / 2) % (2 * Math.PI);
			var off = (this.dist + this.thickness) * renderingRules.scale + 5 * renderingRules.scale;
			ctx.save();
			ctx.font = "".concat(renderingRules.fontPt * renderingRules.scale).concat("pt ").concat(renderingRules.font);
			ctx.fillStyle = "#000000";
			
			ctx.translate(center.x, center.y);
			var flip = (theta < 3 * Math.PI / 2 && theta > Math.PI / 2);
			var x = ctx.measureText(label).width + off;
			ctx.rotate(flip ? theta + Math.PI : theta);
			ctx.fillText(label, flip ? -x : off, 0);
			ctx.restore();
			return x;
		}
		return this.dist + this.thickness;
	}
	
	/**
	 * pre: dist, theta, font, have been scaled and (if applicable)translated to scale = 1.0, center = {x:0,y:0}
	 */
	this.contains = function(dist, theta, ctx, font){
		if(ctx && font){
			ctx.save();
			ctx.font = font;
			var b = dist >= this.dist
					&& dist <= this.dist + this.thickness + ctx.measureText(label).width + 10
					&& theta >= this.start
					&& theta <= this.end;
			ctx.restore();
			return b;
		} else {
			return  dist >= startDistance
					&& dist <= endDistance
					&& theta >= startAngle
					&& theta <= endAngle;
		}
	}
	
	/**
	 * Clones the arc. Recommended if using in two different places.
	 */
	this.clone = function(){
		throw "Not yet supported";
	}
}


/**
 * Curve represents a connection between two polar points. Both points have equal
 * r values, but different t values.
 * Properties:
 * start: The smaller t value
 * end: The larger t value
 * dist: The r value
 * 
 * 
 * render():void
 * 
 * Renders this curve.
 * 
 * @param ctx The canvas context, given by element.getContext("2d")
 * @param center The origin on the plot, cartesian coordinates
 * @param renderingRules The rules by which the plot renders. See RenderingRules
 */
function Curve(startAngle, endAngle, distance){
	this.start = startAngle;
	this.end = endAngle;
	this.dist = distance;
	if(startAngle == endAngle)
		thetaEnd += .01;
	else if(startAngle + Math.PI == endAngle)
		thetaEnd -= .01;
	this.render = function(ctx, center, renderingRules){
		var thetaStart = (startAngle + renderingRules.rotation) % 256;
		var thetaEnd = (endAngle + renderingRules.rotation) % 256;
		var h = renderingRules.scale * this.dist / Math.cos((thetaEnd - thetaStart) / 2);
		var l = renderingRules.scale * this.dist * Math.tan((thetaEnd - thetaStart) / 2);
		var center2 = pointAt(center, h, (thetaEnd + thetaStart) / 2);
		var p1 = pointAt(center, renderingRules.scale * this.dist, thetaEnd);
		var p2 = pointAt(center, renderingRules.scale * this.dist, thetaStart);
		var theta1 = getAngle(center2, p1);
		var theta2 = getAngle(center2, p2);
		
		ctx.save();
		ctx.strokeStyle = renderingRules.curveStroke;
		ctx.moveTo(p1.x, p1.y);
		ctx.arc(center2.x, center2.y, Math.abs(l), theta1, theta2, l < 0);
		ctx.stroke();
		ctx.restore();
	}
}

/**
 * Creates a new RenderingRules object based on another object, rules. Rules
 * may contain the following properties:
 * 
 * curveStroke - The color of curves
 * arcFill - The color of arcs
 * font - The font *type*
 * fontPt - The default font *size*
 * rotation - The rotation of the image
 * scale - The scale of the image
 * 
 * These properties default to (if not defined):
 * curveStroke - "#000000"
 * arcFill - "#0000FF"
 * font - "Verdana"
 * fontPt - 12
 * rotation - 0
 * scale - 1.0
 */
function RenderingRules(rules){
	if(rules.curveStroke){
		this.curveStroke = rules.curveStroke;
	} else {
		this.curveStroke = "#000000";
	}
	if(rules.arcFill){
		this.arcFill = rules.arcFill;
	} else {
		this.arcFill = "#0000FF";
	}
	if(rules.font){
		this.font = rules.font;
	} else {
		this.font = "Verdana";
	}
	if(rules.fontPt){
		this.fontPt = rules.fontPt;
	} else {
		this.fontPt = 12;
	}
	if(rules.rotation){
		this.rotation = rules.rotation;
	} else{
		this.rotation = 0;
	}
	if(rules.scale){
		this.scale = rules.scale;
	} else {
		this.scale = 1.0;
	}
}



/* Math functions */

/**
 * Converts a polar coordinate to a cartesian coordinate.
 * 
 * @param center The translation of the origin of the polar grid from the cartesian grid
 * @param r r
 * @param theta theta
 * @returns a point {x,y}
 */
function pointAt(center, r, theta){
	return {
		x : Math.cos(theta) * r + center.x,
		y : Math.sin(theta) * r + center.y
	};
}

/**
 * Returns the angle from the origin of the point. t in a polar grid.
 */
function getAngle(center, location){
	var x = location.x - center.x;
	var y = location.y - center.y;
	if(x == 0 && y > 0)
		return Math.PI / 2;
	if(x == 0 && y < 0)
		return 3 * Math.PI / 2;
	if(y == 0 && x > 0)
		return 0;
	if(y == 0 && x < 0)
		return Math.PI;
	var angle = Math.atan(y/x);
	if(x > 0 && y > 0)
		return angle;
	if(x < 0)
		return Math.PI + angle
	return Math.PI * 2 + angle;
}

/**
 * Returns polar coordinates,
 * {
 * 	t: the angle
 * 	x: the distance from the origin
 * }
 * 
 * @param p The point to convert, {x, y}
 * @param center The origin of the polar grid relative to the origin of the cartesian grid
 * @param rotation The rotation of the polar grid relative to the rotation of the cartesian grid
 * @param scale The scale of the polar grid relative to the rotation of the caretesian grid 
 */
function convertPoint(p, center, rotation, scale){
	var x = p.x - center.x;
	var y = p.y - center.y;
	var angle = getAngle({x:0,y:0}, {x:x,y:y});
	var dist = Math.sqrt(x * x + y * y) / scale;
	angle -= rotation;
	if(angle < 0){
		angle = Math.PI * 2 + angle;
	}
	angle %= Math.PI * 2;
	console.log();
	if(angle < 0){
		angle += Math.PI * 2;
	}
	return {t:angle,x:dist};
}