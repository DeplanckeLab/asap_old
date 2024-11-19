### Default Parameters
set.seed(42)
input.file <- "/data/asap/users/1/ft92la/normalization/output.tab"
output.folder <- "/data/asap/users/1/ft92la/clustering/6535"
algorithm <- "kmeans"

### Libraries
require(jsonlite)
require(data.table)

### Plots that will be generated
data.plots = NULL

### Functions
sihouette.plot <- function(data.clust.p, data.dist.p){
  require(cluster)
  data.silho <- apply(as.matrix(data.clust.p), 2, function(x) mean(silhouette(x, data.dist.p)[,3]))
  best.k <- names(which(data.silho == max(data.silho)))
  data.plots <<- data.frame(name="silhouette.png",description=paste0("Silhouette plot of estimated best k = ",best.k))
  png(paste0(output.folder,"/silhouette.png"), width=500, height=600, type="cairo")
  plot(silhouette(data.clust.p[,best.k], data.dist.p)) # Beware any mix between "k" and k. It works for now
  dev.off()
  data.clust.p[,best.k]
}

error.json <- function(displayed) {
  stats <- list()
  stats$displayed_error = displayed
  write(toJSON(stats, method="C", auto_unbox=T), file = paste0(output.folder,"/output.json"), append=F)
  stop(displayed)
}

### Read file
if(substring(input.file, nchar(input.file) - 4) == ".json") { # if JSON input
  data.json <- fromJSON(paste(readLines(input.file), collapse=""))
  data.norm <- data.frame(text = data.json$text)
  if(!is.null(data.json$PC1)) data.norm$PC1 = data.json$PC1
  if(!is.null(data.json$PC2)) data.norm$PC2 = data.json$PC2
  if(!is.null(data.json$PC3)) data.norm$PC3 = data.json$PC3
  if(!is.null(data.json$PC4)) data.norm$PC4 = data.json$PC4
  if(!is.null(data.json$PC5)) data.norm$PC5 = data.json$PC5
  row.names(data.norm) <- data.norm$text
  data.norm <- data.norm[,-which(colnames(data.norm) %in% c("text"))]
  data.norm <- data.frame(t(data.norm), check.names = F)
} else {
  data.norm <- fread(input.file, sep="\t", header=T, colClasses=c(Genes="character"), check.names=F, stringsAsFactors=F)
  data.norm = data.frame(data.norm, check.names=F, stringsAsFactors=F)
  row.names(data.norm) = data.norm$Genes
  data.norm = data.norm[,2:ncol(data.norm)]
}

### Handle clusters
if(is.na(8) || 8 == "" || 8 == "auto" || 8 == "0") {
  nbclust <- c(2, min(ncol(data.norm), 21) - 1)
} else nbclust <- as.numeric(strsplit(8, ":")[[1]])
if(length(nbclust) != 1 && length(nbclust) != 2) error.json("This number of clusters does not make sense")
if(length(nbclust) == 1 && nbclust > ncol(data.norm)) error.json("Cannot have more clusters than cells")
if(length(nbclust) == 2 && nbclust[2] > ncol(data.norm)) error.json("Max cluster cannot be greater than number of cells")

### Run clustering algorithm
if (algorithm == "kmeans"){ # default []
  if(length(nbclust) == 1){
    data.clust <- kmeans(t(data.norm), nbclust)
    data.out <- data.clust$cluster
  } else if(length(nbclust) == 2){
    if(nbclust[1] < 2) nbclust[1] <- 2
    data.clust <- sapply(nbclust[1]:nbclust[2],function(i) kmeans(t(data.norm), i)$cluster)
    colnames(data.clust) = nbclust[1]:nbclust[2]
    data.dist <- dist(t(data.norm))
    data.out <- sihouette.plot(data.clust, data.dist)
  }
} else error.json("This clustering method is not implemented.")

stats <- list()
stats$list_plots = data.plots
stats$clusters = as.list(data.out)
write(toJSON(stats, method="C", auto_unbox=T), file = paste0(output.folder,"/output.json"), append=F)
