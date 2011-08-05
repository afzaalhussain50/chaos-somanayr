/*
 * Hashmap of the CHR_OFFSETS of chromosomes in base pairs
 */
var CHR_OFFSETS = new Array();
CHR_OFFSETS["chr1"] = "0";
CHR_OFFSETS["chr2"] = "247249719";
CHR_OFFSETS["chr3"] = "490200868";
CHR_OFFSETS["chr4"] = "689702695";
CHR_OFFSETS["chr5"] = "880975758";
CHR_OFFSETS["chr6"] = "1061833624";
CHR_OFFSETS["chr7"] = "1232733616";
CHR_OFFSETS["chr8"] = "1391555040";
CHR_OFFSETS["chr9"] = "1537829866";
CHR_OFFSETS["chr10"] = "1678103118";
CHR_OFFSETS["chr11"] = "1813477855";
CHR_OFFSETS["chr12"] = "1947930239";
CHR_OFFSETS["chr13"] = "2080279773";
CHR_OFFSETS["chr14"] = "2194422753";
CHR_OFFSETS["chr15"] = "2300791338";
CHR_OFFSETS["chr16"] = "2401130253";
CHR_OFFSETS["chr17"] = "2489957507";
CHR_OFFSETS["chr18"] = "2568732249";
CHR_OFFSETS["chr19"] = "2644849402";
CHR_OFFSETS["chr20"] = "2708661053";
CHR_OFFSETS["chr21"] = "2771097017";
CHR_OFFSETS["chr22"] = "2818041340";
CHR_OFFSETS["chrX"] = "2867732772";
CHR_OFFSETS["chrY"] = "3022646526";
CHR_OFFSETS["chrM"] = "3080419480";
CHR_OFFSETS["hgall"] = "3080436051";

/*
 * Add a contains function 
 */
Array.prototype.contains = function(element) {
	for(var i = 0; i < this.length; i++) {
		if(this[i] == element) {
			return true;
		}
	}
	return false;
}
var chromosomes = ["chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY", "chrM", "hgall"]

var GENOME_END = 3137161264;

/**
 * Internal use only
 *
 * offsetList:
 * curveRadius
 * chrOff
 * chrThickness
 * innerOff
 * innerThickness
 */

function buildPlot(lines, offsetList, w, h) {
	//generate super arc
	var superArc = new Arc(0, Math.PI * 2, offsetList.curveRadius, offsetList.chrOff + offsetList.chrThickness, "");
	
	//allocate memory for chromosome arcs
	var chromosomeArcs = new Array();
	//allocate memory for curves
	var curves = new Array();

	for(var i = 0; i < chromosomes.length; i++) { 	//creates all the chromosome arcs with the names specified
													//in chromsomes with the CHR_OFFSETS specified in CHR_OFFSETS.
													//The size of the arc is proportional to its size on the genome
													//but out of 2 PI instead of 3,137,161,264
		chromosomeArcs[chromosomes[i]] = new Arc(CHR_OFFSETS[chromosomes[i]], (i == chromosomes.length - 1) ? 2 * Math.PI : CHR_OFFSETS[chromosomes[i + 1]], offsetList.chrOff, offsetList.chrOff + offsetList.chrThickness, chromosomes[i]);
	}
	
	superArc.children = chromosomeArcs; //set the children of superArc to the arcs we need to render
	
	for(var i = 1; i < lines.length; i++) { //iterates through all the lines except the first one
		if(lines[i] == "") //stop condition to prevent failures if the file ends in newline(s)
			break; //FIXME: use continue instead???
		var data = getLineData(lines[i]); //line data broken into usable objects
		
		if(chromosomeArcs[data.left.chromosome] == undefined){
			console.log('Left undefined; line ' + i + '; value: ' + data.left.chromosome);
		}
		if(chromosomeArcs[data.right.chromosome] == undefined){
			console.log('Right undefined; line ' + i + '; value: ' + data.right.chromosome);
		}
		
		// define the left side
		var leftArc = new Arc(2 * Math.PI * data.left.start / GENOME_END, 2 * Math.PI * data.left.end / GENOME_END, offsetList.innerOff, offsetList.innerOff + offsetList.innerThickness, "");
		
		if(!chromosomeArcs[data.left.chromosome].children.contains(leftArc)) //check for duplicates
			chromosomeArcs[data.left.chromosome].children.push(leftArc);
			
		//define the right side
		var rightArc = new Arc(2 * Math.PI * data.right.start / GENOME_END, 2 * Math.PI * data.right.end / GENOME_END, offsetList.innerOff, offsetList.innerOff + offsetList.innerThickness, "");
		
		if(!chromosomeArcs[data.right.chromosome].children.contains(rightArc)) //check for duplicates
			chromosomeArcs[data.right.chromosome].children.push(rightArc);
		
		var curve = new Curve(2 * Math.PI * ((data.left.start + data.left.end) / 2) / GENOME_END, 2 * Math.PI * ((data.right.start + data.right.end) / 2) / GENOME_END, offsetList.curveRadius);
		curves.push(curve);
	}
	
	superArc.children = chromosomeArcs;

	return new SelfContainedCircosPlot(superArc, curves, w, h);
}

/*
 * Internal use only. Takes one line and interprets the components, seperated by TAB (\t).
 * Format:
 * 0			1			2			3			4			5
 * chrom_left	start_left	end_left	chrom_right	start_right	end_right 
 */
function getLineData(line) {
	var data = line.split("\t");
	
	var leftStart = CHR_OFFSETS[data[0]] + data[1];
	var leftEnd = CHR_OFFSETS[data[0]] + data[2];

	var rightStart = CHR_OFFSETS[data[3]] + data[4];
	var rightEnd = CHR_OFFSETS[data[3]] + data[5];

	return {
		left : { //the left side
			start : leftStart,
			end : leftEnd,
			chromosome : data[0]
		},
		right : { //the right side
			start : rightStart,
			end : rightEnd,
			chromosome : data[3]
		}
	};
}

/*
 * Thanks to awerti on StackOverflow for giving me a read function!
 * http://stackoverflow.com/questions/6861180/how-can-you-read-a-file-line-by-line-in-javascript
 * 
 * Callback will have one parameter, which is a SelfContainedCircosPlot
 * 
 * file is the name of the file. It MUST be on the same domain.
 * 
 * w is the width
 * 
 * h is the height
 * 
 * offsetList:
 * {
 * 	chrOff: the offset of the of the chromosome Arc
 * 	chrThickness: The thickness of the chromosome Arc
 * 	innerOff: The offset from the center of the inner Arc
 * 	innerThickness: The thickness of the inner Arc
 * 	curveRadius: The distance from the center the Curves should be build
 * }
 */
function buildPlotFromFile(file, offsetList, w, h, callback) {
	var lines;
	var txtFile = new XMLHttpRequest();
	txtFile.open("GET", file, true);
	txtFile.onreadystatechange = function() {
		if(txtFile.readyState === 4) {  // document is ready to parse.
			if(txtFile.status === 200) {  // file is found
				allText = txtFile.responseText;
				lines = txtFile.responseText.split("\n"); // Seperate the text into lines
				callback(buildPlot(lines, offsetList, w, h)); //build plot and pass back to the user
			} else {
				throw "File not found";
			}
		}
	}
	txtFile.send(null);
}