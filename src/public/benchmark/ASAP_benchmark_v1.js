var benchmark = document.getElementById("benchmark"),
jsonDB,
symbol = [100,101,102,103,104,105,106,133,134,135];
regressionOp=0.8;
reference="benchmark_";

init();

function init(){
	Plotly.d3.json("benchmarkDB.json", function(err, json) {
		jsonDB = json;
		for (step in json) {
			generateBenchmarkItem(step);
			benchMark = document.getElementById(step);
			data = prepareData(json[step]);
			t0 = performance.now();
			Plotly.react(benchMark, data, getLayout(step));
			t1 = performance.now();
			console.log(step + ": " + (t1 - t0) + " milliseconds.");
		}
	});
}


function getLayout(step){
	return {
		title: "<b>Benchmark of " + step + "</b>",
		titlefont: {size: 13},
		font: {family: 'Courier New, monospace'},
		margin: { t: 50, l:50, r:10, b:40 },
		yaxis: {title: "Duration [s]",type:"log"},
		xaxis: {title: "Cells*Genes 10<sup>-6</sup>",type:"log"},
		//xaxis: {title: "Cells",type:"log"},
		hovermode: "closest",
		hoverdistance: 20
	};
}

function generateBenchmarkItem(name){
	var div = document.createElement("div");
	div.id=name;
	div.style.width="600px" ;
	div.style.height="500px";
	document.getElementById(getFirstEmptyCell()).appendChild(div);
}

function getFirstEmptyCell(){
	 for(i=0;i<9;i++){
		 if (!document.getElementById(reference + i).hasChildNodes()){
			 return reference+i;
		 }
	 }
}

function prepareData(methodData){
	t0 = performance.now();
	count = Object.keys(methodData).length;
	data = [],i=0;
	var colors = palette("mpn65", count);
	for (method in methodData) {		
		x = [];y = [];id = [];customdata=[],color=[];text=[];
		methodData[method].forEach(function(point){
			x.push(point.duration);
			color.push("#" + colors[i]);
			//y.push(point.nber_rows);
			y.push(point.nber_rows*point.nber_cols/1000000);
			text.push("Cells: " + point.nber_rows + "<br>Genes: " + point.nber_cols + "<br>" + point.job_id);
			id.push(point.job_id);
		});
		//console.log("Adding " + method + " :" + x.length);
		data.push(getTrace(method,x,y,text,id,color, symbol[i]));
		i=i+1;
	}
	t1 = performance.now();
	console.log("Data processing took " + (t1 - t0) + " milliseconds.");
	return data;
}

function getTrace(name, x, y, text, id, color, symbol){
	return {
		name: name,
	 	type: "scattergl",
	 	//opacity:0.8,
	 	mode: "markers",
	 	marker: {
	 		symbol: symbol,
      		size: 8,
        	color: color,
        	opacity: 0.6},
		x: y,
		y: x,
		id: id,
		text: text,
		showlegend: true,
		hoverinfo: "text",
		hoverlabel: {bgcolor:"lightgrey", font:{size:10}}
	};
}
