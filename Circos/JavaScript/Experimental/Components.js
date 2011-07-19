/**
 * 
 * 
 */

"use strict";

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
	this.contains = function(dist, theta, ctx, font, scale){
		ctx.save();
		ctx.font = font;
		var b = dist >= startDistance
				&& dist <= endDistance + ((ctx) ? ctx.measureText(label).width + 10 * scale : 0)
				&& theta >= startAngle
				&& theta <= endAngle;
		ctx.restore();
		return b;
	}
}


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
		var h = renderingRules.scale * distance / Math.cos((thetaEnd - thetaStart) / 2);
		var l = renderingRules.scale * distance * Math.tan((thetaEnd - thetaStart) / 2);
		var center2 = pointAt(center, h, (thetaEnd + thetaStart) / 2);
		var p1 = pointAt(center, renderingRules.scale * distance, thetaEnd);
		var p2 = pointAt(center, renderingRules.scale * distance, thetaStart);
		var theta1 = getAngle(center2, p1);
		var theta2 = getAngle(center2, p2);
		
		/*ctx.save();
		ctx.strokeStyle = color;
		ctx.moveTo(center.x, center.y);
		ctx.lineTo(p1.x, p1.y)
		ctx.stroke();
		ctx.restore();
		
		ctx.save();
		ctx.strokeStyle = color;
		ctx.moveTo(center.x, center.y);
		ctx.lineTo(p2.x, p2.y)
		ctx.stroke();
		ctx.restore();
		
		ctx.save();
		ctx.strokeStyle = color;
		ctx.moveTo(center2.x, center2.y);
		ctx.lineTo(p1.x, p1.y)
		ctx.stroke();
		ctx.restore();
		
		ctx.save();
		ctx.strokeStyle = color;
		ctx.moveTo(center2.x, center2.y);
		ctx.lineTo(p2.x, p2.y)
		ctx.stroke();
		ctx.restore();*/
		
		ctx.save();
		ctx.strokeStyle = renderingRules.curveStroke;
		ctx.moveTo(p1.x, p1.y);
		ctx.arc(center2.x, center2.y, Math.abs(l), theta1, theta2, l < 0);
		ctx.stroke();
		ctx.restore();
	}
}

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
function pointAt(center, l, theta){
	return {
		x : Math.cos(theta) * l + center.x,
		y : Math.sin(theta) * l + center.y
	};
}

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
	console.log(angle);
	console.log();
	if(angle < 0){
		angle += Math.PI * 2;
	}
	return {t:angle,x:dist};
}