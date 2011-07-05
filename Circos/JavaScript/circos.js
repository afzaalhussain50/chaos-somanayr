/*Datasource object:
 * 
 * Settings (optional):
 * 	innerArcThickness - The thickness of the inner arc
 * 	innerTextOffset - 	The offset of the text from the inner arc
 * 	innerTextWidth - 	The max width in pixels of the inner text
 * 	outerArcOffset - 	The offset of the outer arc from the text
 * 	outerArcThickness - The thickness of the outer arc
 * Parameters (optional):
 * 	groupNames - 		The names of the groups, in order of group
 * 	groupEnd - 			The end points of each group, parallel with groupNames
 * Parameters (required):
 * 	colorScheme - 		The set of colors used to create splines, specified by type
 * 	splineOutline - 	The color of the outline of the spline. Usually black
 * 	sweeps - 			The size of the nodes. This is the radians in the circle that the node "sweeps" out
 * 	labels - 			The labels for the nodes. Muts be parallel with sweeps
 * 	dist - 				The distance from the center to the inner arc
 * 	data1 - 			The first part of the relationships
 * 	data2 - 			The second part of the relationships, parallel to data1
 * 	type - 				The type of relationship between data1 and data2, parallel to data1 and data2
 */

"use strict";

function Circos(datasource){
	if ( !(this instanceof Circos) )
      return new Circos();

	/**
	 * DO NOT MODIFY ds!!!!
	 * If you do, be sure to call ds.prepareData();
	 */
	this.ds = new DataSet(datasource);
	
	/**
	 * Call this method ONLY! This method will handle everything you need.
	 */
	this.render = function(ctx, center, dist){
		ctx.save();
		if(dist){
			this.ds.scale = dist / this.ds.distance;
			var innerFontSize = Math.round(this.ds.innerFontSize * this.ds.scale);
			console.log(innerFontSize);
			this.ds.font = "".concat(innerFontSize).concat("pt ").concat(this.ds.innerFontType);
			this.ds.dist = dist;
		}
		
		ctx.fillStyle = "#FFFFFF";
		ctx.fillRect(0,0,500,500);
		
		this.drawArcs(ctx, center);
		this.drawRelationships(ctx, center);
	}
	
	this.drawArcs = function(ctx, center) {
		
		/* inner arcs */
		var t = 0.0;
		var max = 0;
		for(var i = 0; i < this.ds.sweeps.length; i++){
			max = Math.max(max, drawArc(ctx, center, this.ds.dist, t, this.ds.sweeps[i], this.ds.scale * this.ds.innerArcThickness, this.ds.labels[i], this.ds.scale * this.ds.innerTextOffset, this.ds.font));
			t += this.ds.sweeps[i] + this.ds.gap;
		}
		
		this.ds.innerStringMax = max;
		
		/* outer arcs */
		max = 0;
		t = 0.0;
		for(var i = 0; i < this.ds.groupNames.length; i++){
			max = Math.max(max, drawArc(ctx, center, max + this.ds.outerArcOffset, t, this.ds.groupSweep[i], this.ds.scale * this.ds.outerArcThickness, this.ds.groupNames[i], this.ds.scale * this.ds.outerTextOffset, this.ds.font));
			t += this.ds.groupSweep[i] + this.ds.gap;
		}
		
		this.ds.outerStringMax = max;
	}
	
	this.drawRelationships = function(ctx, center) {
		var offsets = new Array();
		for(var i = 0; i < ds.sweeps.length; i++){
			offsets[i] = 0;
		}
		for (var i = 0; i < this.ds.data1.length; i++) {
			
			var d1 = this.ds.data1[i];
			var d2 = this.ds.data2[i];
			
			var s1 = 0;
			for (var j = 0; j < d1; j++) {
				s1 += this.ds.sweeps[j];
			}
			var s2 = 0;
			for (var j = 0; j < d2; j++) {
				s2 += this.ds.sweeps[j];
			}
			var theta1 = s1 + offsets[d1] * this.ds.relSweep[d1] + this.ds.gap * d1;
			var theta2 = s2 + offsets[d2] * this.ds.relSweep[d2] + this.ds.gap * d2;
			offsets[d1]++;
			offsets[d2]++;
			
			var p0a = pointAt(center, this.ds.dist, theta1);
			var p0b = pointAt(center, this.ds.dist, theta1 + this.ds.relSweep[d1]);
			var p1 = center;
			var p2a = pointAt(center, this.ds.dist, theta2 + this.ds.relSweep[d2]);
			var p2b = pointAt(center, this.ds.dist, theta2);
			
			ctx.beginPath();
			ctx.fillStyle = this.ds.colorScheme[this.ds.type[i]];
			ctx.strokeStyle = this.ds.splineOutline;
			ctx.moveTo(p0b.x, p0b.y);
			ctx.arc(center.x, center.y, this.ds.dist, theta1 + this.ds.relSweep[d1], theta1, true);
			//drawSpline(ctx, [p0a, p1, p2a]);
			ctx.bezierCurveTo(p0a.x, p0a.y, p1.x, p1.y, p2a.x, p2a.y);
			ctx.arc(center.x, center.y, this.ds.dist, theta2 + this.ds.relSweep[d2], theta2, true);
			//drawSpline(ctx, [p0b, p1, p2b]);
			ctx.bezierCurveTo(p2b.x, p2b.y, p1.x, p1.y, p0b.x, p0b.y);
			ctx.closePath();
			ctx.fill();
			//ctx.stroke();
		}
	}
	
	this.handleMouseDown = function(ctx, location){
		
	}
	
	this.handleMouseDrag = function(ctx, deltaX, deltaY){
		
	}
	
	this.handleMouseClick = function(ctx, location){
		
	}
	
	this.handleMouseWheel = function(ctx, wheelDelta){
		
	}
}

function drawSpline(ctx, anchors){
	if(anchors.length > 3)
		throw ("Cannot use more than three points.");
	var detail = 1 /
		(
			distance(anchors[0].x, anchors[0].y, anchors[1].x, anchors[1].y)
			+ distance(anchors[1].x, anchors[1].y, anchors[2].x, anchors[2].y)
		);	
	var l1 = new Line(anchors[0], anchors[1]);
	var l2 = new Line(anchors[1], anchors[2]);
	var t;
	ctx.lineTo(anchors[0].x, anchors[0].y)
	for (t = 0.0; t < 1.0; t+=detail) {
		var cur = new Line(
			l1.getPointAtPosition(t),
			l2.getPointAtPosition(t)
		).getPointAtPosition(t);
		ctx.lineTo(cur.x, cur.y);
	}
	ctx.lineTo(anchors[2].x, anchors[2].y);
}

function distance(x1, y1, x2, y2){
	return
		Math.sqrt(
			(x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
		);
}

function distanceP(p1, p2){
	return distance(p1.x, p1.y, p2.x, p2.y);
}

function Line(start, end) {
	this.x1;
	this.y1;
	this.x2;
	this.y2;

	this.x1 = start.x;
	this.y1 = start.y;
	this.x2 = end.x;
	this.y2 = end.y;

	this.getPointAtPosition = function(percent) {
		var x;
		var y;
		if(this.x1 == this.x2)
			x = this.x1;
		else
			x = (percent * (this.x2 - this.x1)) + this.x1;
		if(this.y1 == this.y2)
			y = this.y1;
		else
			y = (percent * (this.y2 - this.y1)) + this.y1;
		return {
			x : x,
			y : y
		};
	}
}


	
function pointAt(center, l, theta){
	return {
		x : Math.cos(theta) * l + center.x,
		y : Math.sin(theta) * l + center.y
	};
}

function drawArc(ctx, center, l, theta, sweep, thickness, label, textOff, font){

	ctx.save();
	
	var p0 = pointAt(center, l, theta);
	var p1 = pointAt(center, l + thickness, theta);
	var p2 = pointAt(center, l + thickness, theta + sweep);
	var p3 = pointAt(center, l, theta + sweep);
	ctx.fillStyle = "yellow";
	
	ctx.beginPath();
	console.log("Center: ".concat(center.x).concat(", ").concat(center.y).concat("; radius: ").concat(l).concat("; theta start: ").concat(theta).concat("; theta sweep: ").concat(sweep).concat("; thickness: ").concat(thickness).concat("; label: ").concat(label).concat("; text offset: ").concat(textOff).concat("; font: ").concat(font));
	ctx.arc(center.x, center.y, l + thickness, theta, theta + sweep, false);
	ctx.arc(center.x, center.y, l, theta + sweep, theta, true)
	ctx.closePath();
	ctx.fill();
	ctx.stroke();
	
	ctx.restore();
	
	return drawText(ctx, center, l + textOff, theta + sweep / 2, label, font);
}

function drawText(ctx, center, off, theta, value, font){
	ctx.save();
	ctx.font = font;
	ctx.fillStyle = "#000000";
	ctx.translate(center.x, center.y);
	var flip = (theta < 3 * Math.PI / 2 && theta > Math.PI / 2);
	var x = ctx.measureText(value).width + off;
	ctx.rotate(flip ? theta + Math.PI : theta);
	ctx.fillText(value, flip ? -x : off, 0);
	ctx.restore();
	return x;
}

function arc(ctx, center, l, thetaStart, thetaEnd, counterClockwise){
	var advance = (counterClockwise) ? -.01 : .01;
	var t = thetaStart;
	while((thetaEnd > thetaStart) ? t < thetaEnd : t > thetaEnd){
		var p = pointAt(center, l, t);
		ctx.lineTo(p.x, p.y);
		t += advance;
	}
}

function drawPoint(ctx, p){
	ctx.fillRect(p.x - 4, p.y - 4, 8, 8);
}
