var offsets = new Array();
offsets["chr1"] = "0";
offsets["chr2"] = "247249719";
offsets["chr3"] = "490200868";
offsets["chr4"] = "689702695";
offsets["chr5"] = "880975758";
offsets["chr6"] = "1061833624";
offsets["chr7"] = "1232733616";
offsets["chr8"] = "1391555040";
offsets["chr9"] = "1537829866";
offsets["chr10"] = "1678103118";
offsets["chr11"] = "1813477855";
offsets["chr12"] = "1947930239";
offsets["chr13"] = "2080279773";
offsets["chr14"] = "2194422753";
offsets["chr15"] = "2300791338";
offsets["chr16"] = "2401130253";
offsets["chr17"] = "2489957507";
offsets["chr18"] = "2568732249";
offsets["chr19"] = "2644849402";
offsets["chr20"] = "2708661053";
offsets["chr21"] = "2771097017";
offsets["chr22"] = "2818041340";
offsets["chrX"] = "2867732772";
offsets["chrY"] = "3022646526";
offsets["chrM"] = "3080419480";
offsets["hgall"] = "3080436051";

Array.prototype.contains = function(element) {
	for(var i = 0; i < this.length; i++) {
		if(this[i] == element) {
			return true;
		}
	}
	return false;
}
var chromosomes = ["chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY", "hgall"]

var genomeEnd;

/**
 * offsetList:
 * curveRadius
 * chrOff
 * chrThickness
 * innerOff
 * innerThickness
 */

function buildPlot(lines, offsetList, w, h) {
	var superArc = new Arc(0, Math.PI * 2, offsetList.curveRadius, offsetList.chrOff + offsetList.chrThickness, "");
	var chromosomeArcs = new Array();
	var curves = new Array();

	for(var i = 0; i < chromosomes.length; i++) {
		chromosomeArcs[chromosomes[i]] = new Arc(offsets[chromosomes[i]], (i == chromosomes.length - 1) ? 2 * Math.PI : offsets[chromosomes[i + 1]], offsetList.chrOff, offsetList.chrOff + offsetList.chrThickness, chromosomes[i]);
	}

	for(var i = 0; i < lines.length; i++) {
		console.log
		var data = getLineData(lines[i]);
		var leftArc = new Arc(2 * Math.PI * data.left.start / genomeEnd, 2 * Math.PI * data.left.end / genomeEnd, offsetList.innerOff, offsetList.innerOff + offsetList.innerThickness, "");
		chromosomeArcs[data.left.chromosome].children.push(leftArc);
		var rightArc = new Arc(2 * Math.PI * data.right.start / genomeEnd, 2 * Math.PI * data.right.end / genomeEnd, offsetList.innerOff, offsetList.innerOff + offsetList.innerThickness, "");
		chromosomeArcs[data.right.chromosome].children.push(rightArc);
		var curve = new Curve(2 * Math.PI * ((data.left.start + data.left.end) / 2) / genomeEnd, 2 * Math.PI * ((data.right.start + data.right.end) / 2) / genomeEnd, offsetList.curveRadius);
	}

	superArc.children = curves;

	return new SelfContainedCircosPlot(superArc, curves, w, h);
}

function getLineData(line) {
	var data = line.split("\t");

	var leftStart = offsets[data[0]] + data[1];
	var leftEnd = offsets[data[0]] + data[2];

	var rightStart = offsets[data[3]] + data[4];
	var rightEnd = offsets[data[3]] + data[5];

	return {
		left : {
			start : leftStart,
			end : leftEnd,
			chromosome : data[0]
		},
		right : {
			start : rightStart,
			end : rightEnd,
			chromsome : data[3]
		}
	};
}

function buildPlotFromFile(file, offsets, w, h, callback) {
	var lines;
	var txtFile = new XMLHttpRequest();
	txtFile.open("GET", file, true);
	txtFile.onreadystatechange = function() {
		if(txtFile.readyState === 4) {  // document is ready to parse.
			if(txtFile.status === 200) {  // file is found
				allText = txtFile.responseText;
				lines = txtFile.responseText.split("\n");
				// Will separate each line into an array
				callback(buildPlot(lines, offsets, w, h));
			} else {
				throw "File not found";
			}
		}
	}
	txtFile.send(null);
}