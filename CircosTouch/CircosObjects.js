/**
 * Contains objects for defining
 *
 * superArc is not rendered, but instead the Arc
 * that holds all other Arcs. Generally, it should
 * be an arc ranging from 0 to 2 * Math.PI. It
 * should hold at least 1 child node.
 * 
 * connections is an array of Curves. Curves
 * should lie within superArc, though this is not
 * stricly controlled
 * 
 * In documentation, ctx is assumed to be the
 * canvas's context object, obtained by
 * 
 * document.getElementByID(canvasID)
 * 		.getContext("2d")
 * 
 * center is assumed to be the translation of the
 * grid relative to the real origin. To better
 * understand this, read about polar grids in
 * Components.js. Center defines the caretesian
 * center of the polar grid.
 * 
 * renderingRules is assumed to be the ruleset
 * used for rendering. Although it does not need
 * to be an instance of RenderingRules, it is
 * highly suggested that an instance of
 * RenderingRules is passed, as RenderingRules
 * contains default values that will make your
 * life easier.
 * 
 * superArc and connections are assumed to be as defined above.
 * 
 * 
 * 
 * All angles are in radiians!
 */

"use strict";

/**
 * This Circos plot object is the easiest to use. It requires 4 parameters.
 * 
 * @param w The width of the canvas.
 * @param h The height of the canvas.
 * 
 * Properties:
 * center: See center in the heading documentation. {x:w/2, y:h/2}
 * renderingRules: See renderingRules in the heading documentation. Default RenderingRules. See RenderingRules.
 * 
 * 
 * render():void
 * 
 * Renders the plot with the given canvas context.
 * 
 * rotate():void
 * 
 * Rotates the plot
 * 
 * @param theta The angle of rotation.
 * 
 * setScale():void
 * 
 * Sets the scale, ignoring any previous values.
 * this.renderingRules.scale = scale;
 * 
 * @param scale The new scale.
 * 
 * 
 * scalePercent():void
 * 
 * Multiplicitavely  modifies the scale
 * this.renderingRules.scale *= percent;
 * 
 * @param percent The percent by which to modify the scale. 100% should be fed as 1.0.
 * 
 * 
 * getPlot():CircosPlot
 * 
 * Returns the CircosPlot object that represents this Circos plot.
 */
function SelfContainedCircosPlot(superArc, connections, w, h){
	this.center = {x:w/2,y:h/2};
	this.renderingRules = new RenderingRules({});
	var plot = new CircosPlot(superArc, connections, this.center, this.renderingRules);
	this.render = function(ctx){
		var matrix = new Array();
		matrix.push(superArc.children);
		var b = true;
		while(b){
			b = false;
			var ar = new Array();
			for (var i=0; i < matrix[matrix.length - 1].length; i++) {
				var children = matrix[matrix.length - 1][i].children;
				for (var j=0; j < children.length; j++) {
					b = true;
					ar.push(children[j]);
				}
			}
			if(ar.length > 0)
				matrix.push(ar);
		}
		var dist = connections[0].dist;
		for(var i = matrix.length - 1; i >= 0; i--){
			var maxSize = 0;
			for (var j=0; j < matrix[i].length; j++) {
				maxSize = Math.max(maxSize, ctx.measureText(matrix[i][j].label).width);
			}
			maxSize += 20;
			var start = dist;
			dist += maxSize;
			for (var j=0; j < matrix[i].length; j++) {
				matrix[i][j].dist = start;
			}
		}
		plot.render(ctx);
	}
	
	this.rotate = function(theta){
		this.renderingRules.rotation += theta;
	}
	
	this.setScale = function(scale){
		this.renderingRules.scale = scale;
	}
	
	this.scalePercent = function(percent){
		this.renderingRules.scale *= percent;
	}
	
	this.getPlot = function(){
		return plot;
	}
	this.simplify = function(){
		var n = connections[0].dist;
		for (var i=0; i < connections.length; i++) {
		  n = Math.min(n, connections[i]);
		}
		var minCurve = 2 * Math.atan((2 * n - n * n) / (2 * n - 2));
		var screen = {x:0, y:0, width:w, height:h};
		var newCurves = new Array();
		var newSuperArc = new Arc(0, 2 * Math.PI, 100, 120, "");
		for(var i=0; i < connections.length; i++){
			if(connections[i].end - connections[i].start > minCurve && isArcVisible(screen, connections[i]))
				newCurves.push(new Curve(connections[i].start, connections[i].end, connections[i].dist));
		}
		function assess(arc, appendTo){
			if(arc.end - arc.start > 1 / arc.dist && isArcVisible(screen, arc)){
				appendTo.children.push(arc);
				for (var i=0; i < arc.children.length; i++) {
					assess(arc.children[i], appendTo.children[appendTo.children.length - 1]);
				}
			}
		}
		
		for (var i=0; i < superArc.children.length; i++) {
			assess(newSuperArc, superArc.children[i]);
		}
		return new SelfContainedCircosPlot(newSuperArc, newCurves, w, h);
	}
	
	function isArcVisible(view, arc){
		var thick = (arc.thickness);
		
		var center2;
		if(thick){
			center2 = center;
		} else {
			/* find center */
			var thetaStart = (startAngle + renderingRules.rotation) % 256;
			var thetaEnd = (endAngle + renderingRules.rotation) % 256;
			var h = renderingRules.scale * this.dist / Math.cos((thetaEnd - thetaStart) / 2);
			var l = renderingRules.scale * this.dist * Math.tan((thetaEnd - thetaStart) / 2);
			var center2 = pointAt(center, h, (thetaEnd + thetaStart) / 2);
		}
		
		var d1 = distanceTo(center2, {x:view.x, y:view.y}) > arc.dist;
		var d2 = distanceTo(center2, {x:view.x, y:view.y + view.height}) > arc.dist;
		var d3 = distanceTo(center2, {x:view.x + view.width, y:view.y}) > arc.dist;
		var d4 = distanceTo(center2, {x:view.x + view.width, y:view.y + view.height}) > arc.dist;
		
		return !(d1 == d2 && d2 == d3 && d3 == d4); //at least one has to be different. If they're all true, then the arc is too close, if they're all false, the arc is too far
	}
	
	function distanceTo(p1, p2){
		var x = (p2.x - p1.x);
		var y = (p2.y - p1.y);
		return Math.sqrt(x * x - y * y);
	}
}

/**
 * This plot object is easier to use than Plot,
 * but still more complex than SelfContainedCircosPlot
 * 
 * setRotataion():void
 * 
 * Changes the rotation ignoring any previous rotation
 * 
 * @param rotation The new angle of rotation
 * 
 * 
 * rotate():void
 * 
 * Changes the rotation by adding the given angle
 * to the previous rotation
 * 
 * @param theta The angle to add
 * 
 * 
 * pan():void
 * 
 * Shifts the center of the plot by (deltaX, deltaY)
 * 
 * @param deltaX the change in the x coordinate
 * @param deltaY the change in the y coordinate
 * 
 * 
 * setCenter():void
 * 
 * Shifts the center to the given point
 * 
 * @param moveTo a point {x,y} that is the new center
 * 
 * 
 * getPlot():Plot
 * 
 * Returns the plot object representing this CircosPlot
 * 
 * 
 * render():void
 * 
 * Draws this CircosPlot
 * 
 * @param ctx see main description
 * 
 * 
 * findHitByEvent():Arc
 * 
 * Finds the arc that collides with this event
 * 
 * @param evt the event that happened
 * @param ctx used for text size computation
 * 
 * 
 * finHitByLocation():Arc
 * 
 * Finds the arc that collides with this position
 * 
 * @param x the canvas x location
 * @param y the canvas y location
 * 
 * 
 * zoomInOnSection():void
 * 
 * Creates a zoomed in picture of this section, ignoring all other parts
 * 
 * @param ctx
 * @param startAngle the starting (smallest) angle of the section
 * @param endAngle the ending (largest) angle of the section
 * @param w the canvas width
 * @param h the canvas height
 * 
 * 
 * createSubPlot:void()
 * 
 * Draws a sub plot from the given positions
 * 
 * @param start the array of start angles
 * @param end the array of end angles
 * @param w the width of the plot
 * @param h the height of the plot
 */
function CircosPlot(superArc, connections, center, renderingRules){
	this.setRotation = function(rotation){
		renderingRules.rotation = rotation;
	}
	this.rotate = function(theta){
		renderingRules.rotation += theta;
		renderingRules.rotation = renderingRules.rotation % (Math.PI * 2);
	}
	this.setScale = function(scale){
		renderingRules.scale = scale;
	}
	this.pan = function(deltaX, deltaY){
		center.x += deltaX;
		center.y += deltaY;
	}
	this.setCenter = function(moveTo){
		center = moveTo;
	}
	var plot = new Plot(superArc, connections);
	this.getPlot = function(){
		return plot;
	}
	this.render = function(ctx){
		plot.render(ctx, center, renderingRules);
	}
	this.findHitByEvent = function(evt, ctx){
		var x = evt.clientX - canvas.offsetLeft;
		var y = evt.clientY - canvas.offsetTop;
		return this.findHitByLocation(x,y, ctx);
	}
	
	this.findHitByLocation = function(x,y, ctx){
		var polarCoords = convertPoint({x:x, y:y}, {x:250,y:250}, renderingRules.rotation, renderingRules.scale);
		var stack = new Array();
		for(var i=0; i < superArc.children.length; i++){
			stack.push(superArc.children[i]);
		}
		while(stack.length > 0){
			var arc = stack.pop();
			if(arc.contains(polarCoords.x, polarCoords.t, ctx, "".concat(renderingRules.fontPt * renderingRules.scale).concat(renderingRules.font), renderingRules.scale)){
				return arc;
			}
			for (var i=0; i < arc.children.length; i++) {
				stack.push(arc.children[i]);
			}
		}
		return null; //no hit
	}
	
	this.zoomInOnSection = function(ctx, startAngle, endAngle, w, h){
		var xIncr = w/2;
		var yIncr = h/2
		var x = xIncr;
		var y = yIncr;
		ctx.save();
		ctx.fillStyle = "#FFFFFF";
		ctx.fillRect(0, 0, w, h);
		var c = 0;
		if(startAngle <= Math.PI / 2 && endAngle >= 0){ //first quadrant
			x -= xIncr;
			y -= yIncr;
			c++;
		}
		if(startAngle <= Math.PI && endAngle >= Math.PI / 2){ //second quadrant
			x += xIncr;
			y -= yIncr;
			c++;
		}
		if(startAngle <= 3 * Math.PI / 2 && endAngle >= Math.PI){ //third quadrant
			x += xIncr;
			y += yIncr;
			c++;
		}
		if(startAngle <= 2 * Math.PI && endAngle >= 3 * Math.PI / 2){ //fourth quadrant
			x -= xIncr;
			y += yIncr;
			c++;
		}
		if(x > w)
			x = w;
		if(x < 0)
			x = 0;
		if(y > h)
			y = h;
		if(y < 0)
			y = 0;
		if(c >= 3){
			x = w/2;
			y = h/2;
		}
		var arc = plot.getSubArc(startAngle, endAngle);
		var stack = new Array();
		stack.push(arc);
		var maxDepth = arc.dist;
		while(stack.length > 0){
			var thisArc = stack.pop();
			maxDepth = Math.min(maxDepth, thisArc.dist);
			for (var i=0; i < thisArc.children.length; i++) {
				stack.push(thisArc.children[i]);
			}
		}
		console.log(maxDepth);
		var scale = Math.min(w/2,h/2) / (arc.dist + arc.thickness + ((ctx) ? ctx.measureText(arc.label).width + 10 : 0));
		var rules = new RenderingRules(renderingRules);
		rules.scale = scale;
		var cons = plot.getSubArcConnections(startAngle, endAngle);
		for (var i=0; i < cons.length; i++) {
			cons[i].dist = maxDepth;
		}
		var p = new Plot(arc, cons);
		p.render(ctx, {x:x,y:y}, rules);
		ctx.restore();
	}
	
	this.createSubPlot = function(ctx, start, end, w, h){
		if(typeof start != "Array"){
			start = [start];
		}
		if(typeof end != "Array"){
			end = [end];
		}
		createSubPlot(ctx, start, end, w, h);
	}
	
	function createSubPlot(ctx, start, end, w, h){
		var ctx2 = document.getElementById("circosPlot2").getContext("2d");
		ctx2.save();
		ctx2.fillStyle = "#FFFFFF";
		ctx2.fillRect(0, 0, w, h);
		plot.subPlot(start, end).render(ctx2, {
			x:w/2,
			y:h/2
		}, renderingRules);
		ctx2.restore();
	}
}

/**
 * The basic, least friendly representation of a Circos Plot.
 * 
 * render():void
 * 
 * Renders this plot
 * 
 * @param ctx
 * @param center
 * @param renderingRules
 * 
 * 
 * getSubArc():Arc
 * 
 * Breaks down the superArc into an Arc within the specified parameters
 * 
 * @param start the start (smallest) angle
 * @param end the end (largest) angle
 * 
 * 
 * getSubArcConnections():Curve *
 * 
 * @param start see getSubArc()
 * @param end see getSubArc()
 * 
 * 
 * subPlot():Plot
 * 
 * Creates a sub-circos plot with the specified parameters.
 * 
 * @param startAngles the array of start (smaller) angles
 * @param endAngles the array of end (larger) angles
 * 
 * startAngles and endAngles act as pairs of a start and end angles.
 * startAngles[i] < endAngles[i]
 * startAngles[i] < startAngles[i] + 1
 * startAngles.length == endAngles.length
 */
function Plot(superArc, connections){
	this.superArc = superArc;
	this.connections = connections;
	this.render = function(ctx, center, renderingRules){
		for (var i=0; i < superArc.children.length; i++) {
			renderArc(ctx, superArc.children[i], center, renderingRules);
		}
		for(var i=0; i < connections.length; i++){
			connections[i].render(ctx, center,  renderingRules);
		}
	}
	
	function renderArc(ctx, arc, center, renderingRules){
		arc.render(ctx, center, renderingRules);
		for (var i=0; i < arc.children.length; i++) {
			renderArc(ctx, arc.children[i], center, renderingRules);
		}
	}
	
	this.getSubArc = function(start, end){
		return getSubArc(superArc, start, end);
	}
	
	this.getSubArcConnections = function(start, end){
		return subConnections(connections, [start], [end], 1.0)
	}
	
	function getSubArc(arc, start, end){
		var newStart, newEnd;
		if(arc.start > end || arc.end < start){
			return null;
		}
		newStart = Math.max(arc.start, start);
		newEnd = Math.min(arc.end, end);
		var children = new Array();
		for (var i=0; i < arc.children.length; i++) {
			var child = getSubArc(arc.children[i], start, end);
			if(child != null){
				children.push(child);
			}
		}
		var ret = new Arc(newStart, newEnd, arc.dist, arc.dist + arc.thickness, arc.label);
		ret.children = children;
		return ret;
	}
	
	this.subPlot = function(startAngles, endAngles){
		var sum = 0.0;
		for (var i=0; i < startAngles.length; i++) {
			sum += endAngles[i] - startAngles[i];
		}
		var scale = (2 * Math.PI - .1) / sum;
		var newSuperArc = subArc(superArc, startAngles, endAngles, scale);
		
		var stack = new Array();
		stack.push(newSuperArc);
		var maxDepth = newSuperArc.dist;
		while(stack.length > 0){
			var thisArc = stack.pop();
			maxDepth = Math.min(maxDepth, thisArc.dist);
			for (var i=0; i < thisArc.children.length; i++) {
				stack.push(thisArc.children[i]);
			}
		}
		
		var newCurves = subConnections(connections, startAngles, endAngles, scale);
		
		for (var i=0; i < newCurves.length; i++) {
			newCurves[i].dist = maxDepth;
		}
		
		return new Plot(newSuperArc, newCurves);
	}
	
	function subArc(arc, startAngles, endAngles, scale){
		var start = undefined;
		var sweep = 0;
		var i = 0;
		for (var i=0; i < startAngles.length; i++) {
			if(endAngles[i] < arc.start || startAngles[i] > arc.end){
				continue;
			}
			if(start == undefined){
				start = Math.max(arc.start, startAngles[i]);
			}
			var thisStart = Math.max(arc.start, startAngles[i]);
			var thisEnd = Math.min(arc.end, endAngles[i]);
			sweep += thisEnd - thisStart;
		}
		var children = new Array();
		for (var i=0; i < arc.children.length; i++) {
			var child = subArc(arc.children[i], startAngles, endAngles, scale);
			if(child != null)
				children.push(child);
		}
		if(start == undefined || sweep == 0)
			return null;
		var arc = new Arc(start * scale, (start + sweep) * scale, arc.dist, arc.dist + arc.thickness, arc.label);
		arc.children = children;
		return arc;
	}
	
	function subConnections(connecs, startAngles, endAngles, scale, verbose){
		var ret = new Array();
		for (var i=0; i < connecs.length; i++) {
			var s = connecs[i].start;
			var e = connecs[i].end;
			var sGood = false;
			var eGood = false;
			for (var j=0; j < startAngles.length; j++) {
				if(startAngles[j] <= s && endAngles >= s){
					sGood = true;
				}
				if(startAngles[j] <= e && endAngles >= e){
					eGood = true;
				}
				if(eGood && sGood){
					if(verbose == true){
					}
					ret.push(new Curve(s * scale, e * scale, connecs[i].dist));
					break;
				}
			}
		}
		return ret;
	}
}