"use strict";

function SelfContainedCircosPlot(superArc, connections, w, h){
	this.center = {x:w/2,y:h/2};
	this.renderingRules = new RenderingRules({});
	this.upperGap = 5;
	this.lowerGap = 5;
	var plot = new CircosPlot(superArc, connections, this.center, this.renderingRules);
	this.render = function(ctx){
		/*var size = 0;
		var stack = new Array();
		for (var i=0; i < superArc.children.length; i++) {
			stack[i] = superArc.children[i];
		}
		while(stack.length > 0){
			var arc = stack.pop();
			size = Math.max(size, ctx.measureText(arc.label).width);
			for (var i=0; i < arc.children.length; i++) {
				stack.push(arc.children[i]);
			}
		}*/
		var matrix = new Array();
		matrix.push(superArc.children);
		var b = true;
		while(b){
			b = false;
			var ar = new Array();
			for (var i=0; i < matrix[matrix.length - 1].length; i++) {
				var children = matrix[matrix.length - 1][i].children;
				for (var j=0; j < children.length; j++) {
					ar.push(children[j]);
				}
			}
			matrix.push(ar);
		}
		console.log(matrix);
		var dist = connections[0].dist;
		for(var i = matrix.length - 1; i >= 0; i--){
			var maxSize = 0;
			console.log(matrix[i]);
			console.log(matrix.length);
			console.log(i);
			for (var j=0; j < matrix[i].length; j++) {
				maxSize = Math.max(maxSize, ctx.measureText(matrix[i][j].label).width);
				console.log(ctx.measureText(matrix[i][j].label).width);
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
	
	/**
	 * Multiplicative scale
	 *
	 * percent should be reduced. For 1.0, do not feed in 100%.
	 */
	this.scalePercent = function(percent){
		this.renderingRules.scale *= percent;
	}
	
	this.getPlot = function(){
		return plot;
	}
}

/**
 * Super arc is not rendered
 */
function CircosPlot(superArc, connections, center, renderingRules){
	this.setRotation = function(rotation){
		renderingRules.rotation = rotation;
	}
	this.rotate = function(deltaTheta){
		renderingRules.rotation += deltaTheta;
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
		var scale = Math.min(w/2,h/2) / (arc.dist + arc.thickness + ((ctx) ? ctx.measureText(arc.label).width + 10 : 0));
		var rules = new RenderingRules(renderingRules);
		rules.scale = scale;
		var cons = plot.getSubArcConnections(startAngle, endAngle)
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

function Plot(superArc, connections){
	this.superArc = superArc;
	this.connection = connections;
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
		
		var newCurves = subConnections(connections, startAngles, endAngles, scale);
		
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