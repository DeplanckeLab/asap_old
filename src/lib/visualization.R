### ASAP Visualization script
options(echo=TRUE)
args <- commandArgs(trailingOnly = TRUE)

### Default Parameters
set.seed(42) # For Tsne
input.file <- args[1]
output.folder <- args[2]
algorithm <- args[3]

### Libraries
require(jsonlite) # BUGS with this library that randomly modify some FDR/pvalues in output JSON
#require(RJSONIO)
require(data.table)

### Functions
error.json <- function(displayed) {
  stats <- list()
  stats$displayed_error = displayed
  write(toJSON(stats, method="C", auto_unbox=T), file = paste0(output.folder,"/output.json"), append=F)
  stop(displayed)
}

### Read file
data.norm <- fread(input.file, sep="\t", header=T, colClasses=c(Genes="character"), check.names=F, stringsAsFactors=F)
data.norm = data.frame(data.norm, check.names=F, stringsAsFactors=F)
row.names(data.norm) = data.norm$Genes
data.norm = data.norm[,2:ncol(data.norm)]
data.norm <- data.norm[apply(data.norm, 1, var, na.rm=TRUE) != 0,] # Remove lines without variation
data.warnings <- NULL
nb.dims <- min(5, ncol(data.norm))
gc()

### Run dimension reduction algorithm
if (algorithm == "pca"){ # default []
  data.pca <- prcomp(t(data.norm), center=T, scale=T) # Default behaviour we chose is to center and scale the data
  data.out <- data.pca$x[,1:nb.dims]
} else if (algorithm == "tsne"){
  require(Rtsne)
  
  perp.max <- (ncol(data.norm) - 1) / 3
  perp <- perp.max
  if(!is.na(args[4]) && args[4] != "") perp = as.double(args[4])
  if(perp > perp.max){
    data.warnings <- data.frame(name=paste0("Perplexity is too high. Set to ", perp.max), description=paste0("Perplexity should not be over nbCell/3"))
    perp <- perp.max
  }
  thet = 0.5
  if(!is.na(args[5]) && args[5] != "") thet = as.double(args[5])
  for(nb.dims in 2:3){
	data.tsne <- NULL
	tryCatch({
		data.tsne <<- Rtsne(t(data.norm), dims = nb.dims, theta = thet, check.duplicates = F, perplexity = perp)
	}, error = function(err) {
		if(grepl("Remove duplicates", err$message)) error.json("Remove duplicates before running t-SNE")
		error.json(err$message)
	})
	data.out <- data.tsne$Y
	rownames(data.out) = colnames(data.norm)
	  
	### Generate Stats struct
	stats <- list()
	stats$text = rownames(data.out)
	if(nb.dims >= 1) stats$PC1 = as.vector(data.out[,1])
	if(nb.dims >= 2) stats$PC2 = as.vector(data.out[,2])
	if(nb.dims >= 3) stats$PC3 = as.vector(data.out[,3])
	if(nb.dims >= 4) stats$PC4 = as.vector(data.out[,4])
	if(nb.dims >= 5) stats$PC5 = as.vector(data.out[,5])
	stats$warnings = as.list(data.warnings)

	### Write stats into JSON
	write(toJSON(stats, method="C", auto_unbox=T, digits = NA), file = paste0(output.folder,"/output.",nb.dims,"d.json"), append=F)
  }
  stop()
} else if (algorithm == "mds"){
  nb.dims <- min(5, ncol(data.norm) - 1)
  type.algorithm = "classic"
  if(!is.na(args[4]) && args[4] != "") type.algorithm = args[4]
  dist.method <- "euclidean"
  if(!is.na(args[5]) & args[5] != "") dist.method <- args[5]
  if(dist.method == "pearson" || dist.method == "spearman") { # Distance matrix
    data.dist <- as.dist(1 - cor(data.norm, method = dist.method))
  } else data.dist <- dist(t(data.norm), method=dist.method)
  if(type.algorithm == "classic") fit <- cmdscale(data.dist, eig=TRUE, k=nb.dims) # k is the number of dim
  if(type.algorithm == "non_metric") {
    require(MASS)
    fit <- isoMDS(data.dist, k=nb.dims) # k is the number of dim
  }
  data.out <- fit$points
} else error.json("This dimension reduction method is not implemented.")

### Generate Stats struct
stats <- list()
stats$text = rownames(data.out)
if(nb.dims >= 1) stats$PC1 = as.vector(data.out[,1])
if(nb.dims >= 2) stats$PC2 = as.vector(data.out[,2])
if(nb.dims >= 3) stats$PC3 = as.vector(data.out[,3])
if(nb.dims >= 4) stats$PC4 = as.vector(data.out[,4])
if(nb.dims >= 5) stats$PC5 = as.vector(data.out[,5])
stats$warnings = as.list(data.warnings)

### Write stats into JSON
write(toJSON(stats, method="C", auto_unbox=T, digits = NA), file = paste0(output.folder,"/output.json"), append=F)
