
/**
 * This object is used to organize the data in a circos plot so that the program can understand it.
 * It stores certain data pieces and has default settings which kick in when not pre-defind by the
 * user.
 */
function DataSet(datasource){	
	if ( !(this instanceof DataSet) )
      return new DataSet();

	/* DECLARE */
	
	/* Settings */
	this.innerArcThickness;
	this.innerTextOffset;
	this.outerArcOffset;
	this.outerArcThickness;
	
	/* Optional paremters */
	this.groupNames;
	this.groupEnd;
	
	/* Required parameters */
	this.colorScheme;
	this.splineOutline;
	this.sweeps;
	this.labels;
	this.dist;
	this.data1;
	this.data2;
	this.type;
	
	/* Cached values */
	this.relSweep;
	this.gap;
	this.groupSweep;
	
	/* Functions */
	this.organizeData
	
	
	/* DEFINE */
	
	/* Define possible undefined settings, set those that aren't there to defaults */
	if(!datasource.innerArcThickness){
		this.innerArcThickness = 10;
	} else {
		this.innerArcThickness = datasource.innerArcThickness;
	}
	if(!datasource.innerTextOffset){
		this.innerTextOffset = 5;
	} else {
		this.innerTextOffset = datasource.innerTextOffset;
	}
	if(!datasource.outerArcOffset){
		this.outerArcOffset = 5;
	} else {
		this.outerArcOffset = datasource.outerArcOffset;
	}
	if(!datasource.outerArcThickness){
		this.outerArcThickness = 5;
	} else {
		this.outerArcThickness = datasource.outerArcThickness;
	}
	if(!datasource.outerTextOffset){
		this.outerTextOffset = 5;
	} else {
		this.outerTextOffset = datasource.outerTextOffset;
	}
	
	/* this isn't a setting, but it doesn't have to be defined */
	if(datasource.groupNames){
		this.groupNames = datasource.groupNames;
	} else {
		this.groupNames = null;
	}
	if(datasource.groupEnd){
		this.groupEnd = datasource.groupEnd;
		if(!this.groupNames){
			throw "Cannot define group endpoints if group names is not defined";
		} else if (this.groupNames.length != this.groupEnd.length){
			throw "Cannot define group endpoints if group names are not parallel";
		}
	} else {
		this.groupEnd = null;
	}
	
	/* Copy data that should be there, check that it really is */
	if(datasource.colorScheme){
		this.colorScheme = datasource.colorScheme;
	} else {
		throw "Color Scheme is undefined";
	}
	if(datasource.splineOutline){
		this.splineOutline = datasource.splineOutline;
	} else {
		throw "Spline outline color is undefined";
	}
	if(datasource.sweeps){
		this.sweeps = datasource.sweeps;
	} else {
		throw "Sweep sweeps are undefined";
	}
	if(datasource.labels){
		this.labels = datasource.labels;
	} else {
		throw "Labels are undefined";
	}
	if(datasource.dist){
		this.dist = datasource.dist;
	} else {
		throw "Inner distance is undefined";
	}
	if(datasource.data1){
		this.data1 = datasource.data1;
	} else {
		throw "Relationship data array 1 is undefined";
	}
	if(datasource.data2){
		this.data2 = datasource.data2;
	} else {
		throw "Relationship data array 2 is undefined";
	}
	if(datasource.type){
		this.type = datasource.type;
	} else {
		throw "Relationship type array is undefined";
	}
	
	
	/* VALIDATE */
	
	/* Validate arcs */
	var sum = 0;
	for (var i = 0; i < this.sweeps.length; i++) {
		sum += this.sweeps[i];
	}
	if(sum > Math.PI * 2){
		throw "sweeps sum to " + sum + ", which is greater than 2 * pi";
	}
	
	/* Validate group names */
	if(this.groupNames != null){
		for (var i = 0; i < this.groupNames.length; i++) { //check for overlapping group names
			for (var j = i + 1; j < this.groupNames.length; j++) {
				if(this.groupNames[i] == this.groupNames[j]){
					throw "Groups " + i + " and " + j + " have the same names";
				}
			}
		}
		for (var i = 1; i < this.groupEnd.length; i++) { //check groups are in order
			if(this.groupEnd[i - 1] >= this.groupEnd[i]){
				throw "Group " + i + " is out of order";
			}
		}
		if(this.groupEnd[this.groupEnd.length - 1] != this.sweeps.length - 1){ //check that everything is represented by a group
			throw "Not everything is represented by a group";
		}
	}
	
	/* Validate relationship data */
	if(this.data1.length != this.data2.length){
		throw "Relationship sweeps are inconsistent: ".concat(this.data1.length).concat(" != ").concat(this.data2.length);
	}
	/*for (var i = 0; i < this.data1.length; i++) {
		if(this.data1[i] == this.data2[i]){
			throw "Relationship ".concat(i).concat(" links to itself");
		}
		for (var j = i + 1; j < this.data1.length; j++){
					if((this.data1[i] == this.data1[j]) && (this.data2[i] == this.data2[j]) //check if relationship is the same
					|| (this.data2[i] == this.data1[j]) && (this.data1[i] == this.data2[j]) //check if relationship is flipped
			){/* end if, begin then *//*
				throw "Duplicate relationship ".concat(i).concat(" and ").concat(j); //this ONLY IF the above conditions are true
			/* no else *//*
			}
		}
	}*/
	
	/* Organize data */
	this.organizeData = function(){
		/* Correct order in relationships */
		for (var i = 0; i < this.data1.length; i++) {
			if(this.data1[i] > this.data2[i]){
				//swap
				var temp = this.data1[i];
				this.data1[i] = this.data2[i];
				this.data2[i] = temp;
			}
		}
		/* Correct order of relationships with bubble sort based on data1 */
		var b = true;
		while(b){
			b = false;
			for (var i = 1; i < this.data1.length; i++) {
				if(this.data1[i - 1] > this.data1[i]){
					b = true;
					
					//swap data1
					var temp = this.data1[i];
					this.data1[i] = this.data1[i - 1];
					this.data1[i - 1] = temp;

					//swap data2
					temp = this.data2[i];
					this.data2[i] = this.data2[i - 1];
					this.data2[i - 1] = temp;
				}
			}
		}
		
		/* Correct the order based on data2 */
		while(b){
			b = false;
			for (var i = 1; i < this.data2.length; i++) {
				if(this.data2[i - 1] < this.data2[i] && this.data1[i - 1] == this.data1[i]){
					b = true;
					
					//swap data1
					var temp = this.data1[i];
					this.data1[i] = this.data1[i - 1];
					this.data1[i - 1] = temp;

					//swap data2
					temp = this.data2[i];
					this.data2[i] = this.data2[i - 1];
					this.data2[i - 1] = temp;
				}
			}
		}
	}
	
	this.prepareData = function() {
		this.organizeData();
		var relCount = new Array(); //helps identify how wide splines should be
		for (var i=0; i < this.sweeps.length; i++) {
			relCount[i] = 0;
		}
		for (var i = 0; i < this.data1.length; i++) {
			relCount[this.data1[i]]++;
			relCount[this.data2[i]]++;
		}

		this.relSweep = new Array(); //relative sweep of each curve in each node
		for (var i = 0; i < relCount.length; i++) {
			this.relSweep[i] = this.sweeps[i] / relCount[i];
		}

		this.gap = this.calcGap(); //cache gap for CPU efficiency
		
		this.groupSweep = new Array();
		var off = 0;
		for (var i = 0; i < this.groupNames.length; i++) {
			var t;
			for(t = 0.0; off <= this.groupEnd[i]; off++){
				t += this.sweeps[off] + this.gap;
			}
			this.groupSweep[i] = t - this.gap; //problematic if group size is 0, which is why that's not allowed
		}
	}
	
	this.calcGap = function(){
		var sum = 0.0;
		for(var i = 0; i < this.sweeps.length; i++){
			sum += this.sweeps[i];
		}
		return (Math.PI * 2 - sum) / this.sweeps.length;
			
	}
	
	this.prepareData();
}