options(repos = c(CRAN = "https://cloud.r-project.org"))
install.packages("remotes")
remotes::install_version("rlang", version="0.1.1", upgrade = "never", dependencies = F)
remotes::install_version("Rcpp", version="0.12.11", upgrade = "never", dependencies = F)
remotes::install_version("RcppArmadillo", version="0.7.900.2.0", upgrade = "never", dependencies = F)
remotes::install_version("bitops", version="1.0-6", upgrade = "never", dependencies = F)
remotes::install_version("RCurl", version="1.95-4.8", upgrade = "never", dependencies = F)
remotes::install_version("Rtsne", version="0.13", upgrade = "never", dependencies = F)
remotes::install_version("MASS", version="7.3-47", upgrade = "never", dependencies = F)
remotes::install_version("RColorBrewer", version="1.1-2", upgrade = "never", dependencies = F)
remotes::install_version("colorspace", version="1.3-2", upgrade = "never", dependencies = F)
remotes::install_version("munsell", version="0.4.3", upgrade = "never", dependencies = F)
remotes::install_version("gtable", version="0.2.0", upgrade = "never", dependencies = F)
remotes::install_version("digest", version="0.6.12", upgrade = "never", dependencies = F)
remotes::install_version("memoise", version="1.1.0", upgrade = "never", dependencies = F)
remotes::install_version("htmltools", version="0.3.6", upgrade = "never", dependencies = F)
remotes::install_version("pkgconfig", version="2.0.1", upgrade = "never", dependencies = F)
remotes::install_version("magrittr", version="1.5", upgrade = "never", dependencies = F)
remotes::install_version("lazyeval", version="0.2.0", upgrade = "never", dependencies = F)
remotes::install_version("tibble", version="1.3.3", upgrade = "never", dependencies = F)
remotes::install_version("stringi", version="1.1.5", upgrade = "never", dependencies = F)
remotes::install_version("stringr", version="1.2.0", upgrade = "never", dependencies = F)
remotes::install_version("glue", version="1.1.1", upgrade = "never", dependencies = F)
remotes::install_version("plyr", version="1.8.4", upgrade = "never", dependencies = F)
remotes::install_version("reshape2", version="1.4.2", upgrade = "never", dependencies = F)
remotes::install_version("assertthat", version="0.2.0", upgrade = "never", dependencies = F)
remotes::install_version("bindr", version="0.1", upgrade = "never", dependencies = F)
remotes::install_version("R6", version="2.2.2", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/plogr/plogr_0.1-1.tar.gz", repos = NULL) # What version should it be? bindrcpp 0.2 requires plogr
remotes::install_version("bindrcpp", version="0.2", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/BH/BH_1.62.0-1.tar.gz", repos = NULL) # What version should it be?
install.packages("https://cran.r-project.org/src/contrib/Archive/dichromat/dichromat_2.0-0.tar.gz", repos = NULL) # What version should it be? scales 0.4.1 requires dichromat
install.packages("https://cran.r-project.org/src/contrib/Archive/labeling/labeling_0.3.tar.gz", repos = NULL) # What version should it be? scales 0.4.1 requires labeling
remotes::install_version("scales", version="0.4.1", upgrade = "never", dependencies = F)
remotes::install_version("pheatmap", version="1.0.8", upgrade = "never", dependencies = F)
remotes::install_version("ggplot2", version="2.2.1", upgrade = "never", dependencies = F)
remotes::install_version("gridExtra", version="2.2.1", upgrade = "never", dependencies = F)
remotes::install_version("viridisLite", version="0.2.0", upgrade = "never", dependencies = F)
remotes::install_version("viridis", version="0.4.0", upgrade = "never", dependencies = F)
remotes::install_version("RJSONIO", version="1.3-1.2", upgrade = "never", dependencies = F)
remotes::install_version("jsonlite", version="1.6", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/yaml/yaml_2.1.14.tar.gz", repos = NULL) # What version should it be? htmlwidgets 0.8 requires yaml
remotes::install_version("htmlwidgets", version="0.8", upgrade = "never", dependencies = F)
remotes::install_version("png", version="0.1-7", upgrade = "never", dependencies = F)
remotes::install_version("base64enc", version="0.1-3", upgrade = "never", dependencies = F)
remotes::install_version("modeltools", version="0.2-21", upgrade = "never", dependencies = F)
remotes::install_version("flexmix", version="2.3-13", upgrade = "never", dependencies = F)
remotes::install_version("mvtnorm", version="1.0-6", upgrade = "never", dependencies = F)
remotes::install_version("DEoptimR", version="1.0-8", upgrade = "never", dependencies = F)
remotes::install_version("robustbase", version="0.92-7", upgrade = "never", dependencies = F)
remotes::install_version("mgcv", version="1.8-23", upgrade = "never", dependencies = F)
remotes::install_version("nlme", version="3.1-131.1", upgrade = "never", dependencies = F)
remotes::install_version("iterators", version="1.0.8", upgrade = "never", dependencies = F)
remotes::install_version("foreach", version="1.4.3", upgrade = "never", dependencies = F)
remotes::install_version("lattice", version="0.20-35", upgrade = "never", dependencies = F)
remotes::install_version("matrixStats", version="0.52.2", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/irlba/irlba_2.1.1.tar.gz", repos = NULL) # What version should it be? igraph 1.1.1 requires irlba version 2.1.1 or higher
remotes::install_version("igraph", version="1.1.1", upgrade = "never", dependencies = F)
remotes::install_version("networkD3", version="0.4", upgrade = "never", dependencies = F)
remotes::install_version("cluster", version="2.0.6", upgrade = "never", dependencies = F)
remotes::install_version("data.table", version="1.10.4", upgrade = "never", dependencies = F)
remotes::install_version("backports", version="1.1.0", upgrade = "never", dependencies = F)
remotes::install_version("Formula", version="1.2-1", upgrade = "never", dependencies = F)
remotes::install_version("latticeExtra", version="0.6-28", upgrade = "never", dependencies = F)
remotes::install_version("acepack", version="1.4.1", upgrade = "never", dependencies = F)
remotes::install_version("checkmate", version="1.8.3", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/evaluate/evaluate_0.10.tar.gz", repos = NULL) # What version should it be? knitr 1.16 requires evaluate version 0.10 or higher
install.packages("https://cran.r-project.org/src/contrib/Archive/highr/highr_0.6.tar.gz", repos = NULL) # What version should it be? knitr 1.16 requires highr version 0.6 or higher
remotes::install_version("mime", version="0.5", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/markdown/markdown_0.7.7.tar.gz", repos = NULL) # What version should it be? knitr 1.16 requires markdown
remotes::install_version("knitr", version="1.16", upgrade = "never", dependencies = F)
remotes::install_version("htmlTable", version="1.9", upgrade = "never", dependencies = F)
remotes::install_version("Hmisc", version="4.0-3", upgrade = "never", dependencies = F)
remotes::install_version("httpuv", version="1.3.5", upgrade = "never", dependencies = F)
remotes::install_version("xtable", version="1.8-2", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/sourcetools/sourcetools_0.1.5.tar.gz", repos = NULL) # What version should it be? shiny 1.0.3 requires sourcetools
remotes::install_version("shiny", version="1.0.3", upgrade = "never", dependencies = F)
remotes::install_version("shinydashboard", version="0.6.1", upgrade = "never", dependencies = F)
remotes::install_version("gtools", version="3.5.0", upgrade = "never", dependencies = F)
remotes::install_version("gdata", version="2.18.0", upgrade = "never", dependencies = F)
remotes::install_version("RMTstat", version="0.3", upgrade = "never", dependencies = F)
remotes::install_version("doParallel", version="1.0.10", upgrade = "never", dependencies = F)
remotes::install_version("caTools", version="1.17.1", upgrade = "never", dependencies = F)
remotes::install_version("gplots", version="3.0.1", upgrade = "never", dependencies = F)
remotes::install_version("ROCR", version="1.0-7", upgrade = "never", dependencies = F)
remotes::install_version("blob", version="1.1.0", upgrade = "never", dependencies = F)
remotes::install_version("WriteXLS", version="4.0.0", upgrade = "never", dependencies = F)
remotes::install_version("brew", version="1.0-6", upgrade = "never", dependencies = F)
remotes::install_version("survival", version="2.41-3", upgrade = "never", dependencies = F)
remotes::install_version("registry", version="0.3", upgrade = "never", dependencies = F)
remotes::install_version("MatrixModels", version="0.4-1", upgrade = "never", dependencies = F)
remotes::install_version("Rook", version="1.1-1", upgrade = "never", dependencies = F)
remotes::install_version("SparseM", version="1.77", upgrade = "never", dependencies = F)
remotes::install_version("DBI", version="0.7", upgrade = "never", dependencies = F)
remotes::install_version("pkgmaker", version="0.22", upgrade = "never", dependencies = F)
remotes::install_version("rngtools", version="1.2.4", upgrade = "never", dependencies = F)
remotes::install_version("foreign", version="0.8-69", upgrade = "never", dependencies = F)
remotes::install_version("bit", version="1.1-12", upgrade = "never", dependencies = F)
remotes::install_version("ff", version="2.2-13", upgrade = "never", dependencies = F)
remotes::install_version("distillery", version="1.0-4", upgrade = "never", dependencies = F)
remotes::install_version("XML", version="3.98-1.9", upgrade = "never", dependencies = F)
remotes::install_version("nnet", version="7.3-12", upgrade = "never", dependencies = F)
remotes::install_version("locfit", version="1.5-9.1", upgrade = "never", dependencies = F)
remotes::install_version("bit64", version="0.9-7", upgrade = "never", dependencies = F)
remotes::install_version("RSQLite", version="2.0", upgrade = "never", dependencies = F)
remotes::install_version("doRNG", version="1.6.6", upgrade = "never", dependencies = F)
remotes::install_version("quantreg", version="5.33", upgrade = "never", dependencies = F)
remotes::install_version("beeswarm", version="0.2.3", upgrade = "never", dependencies = F)
remotes::install_version("e1071", version="1.6-8", upgrade = "never", dependencies = F)
remotes::install_version("statmod", version="1.4.30", upgrade = "never", dependencies = F)
remotes::install_version("pcaPP", version="1.9-72", upgrade = "never", dependencies = F)
remotes::install_version("Matrix", version="1.2-10", upgrade = "never", dependencies = F)
remotes::install_version("nloptr", version="1.0.4", upgrade = "never", dependencies = F)
remotes::install_version("minqa", version="1.2.4", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/RcppEigen/RcppEigen_0.3.2.9.0.tar.gz", repos = NULL) # What version should it be? lme4 1.1-13 requires RcppEigen >= 0.3.2.0.0
remotes::install_version("lme4", version="1.1-13", upgrade = "never", dependencies = F)
remotes::install_version("pbkrtest", version="0.4-7", upgrade = "never", dependencies = F)
remotes::install_version("car", version="2.1-5", upgrade = "never", dependencies = F)
remotes::install_version("Lmoments", version="1.2-3", upgrade = "never", dependencies = F)
remotes::install_version("extRemes", version="2.0-8", upgrade = "never", dependencies = F)
remotes::install_version("rrcov", version="1.4-3", upgrade = "never", dependencies = F)
remotes::install_version("KernSmooth", version="2.23-15", upgrade = "never", dependencies = F)
remotes::install_version("vipor", version="0.4.5", upgrade = "never", dependencies = F)
remotes::install_version("codetools", version="0.2-15", upgrade = "never", dependencies = F)
remotes::install_version("rjson", version="0.2.15", upgrade = "never", dependencies = F)
remotes::install_version("rpart", version="4.1-11", upgrade = "never", dependencies = F)
remotes::install_version("class", version="7.3-14", upgrade = "never", dependencies = F)
remotes::install_version("Cairo", version="1.5-9", upgrade = "never", dependencies = F)
remotes::install_version("ggbeeswarm", version="0.5.3", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/mclust/mclust_5.2.tar.gz", repos = NULL) # What version should it be? fpc 2.1-10 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/prabclus/prabclus_2.2-6.tar.gz", repos = NULL) # What version should it be? fpc 2.1-10 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/diptest/diptest_0.75-7.tar.gz", repos = NULL) # What version should it be? fpc 2.1-10 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/kernlab/kernlab_0.9-25.tar.gz", repos = NULL) # What version should it be? fpc 2.1-10 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/trimcluster/trimcluster_0.1-2.tar.gz", repos = NULL) # What version should it be? fpc 2.1-10 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/fpc/fpc_2.1-10.tar.gz", repos = NULL) # What version should it be? dendextend 1.3.0 requires fpc
install.packages("https://cran.r-project.org/src/contrib/Archive/whisker/whisker_0.3-2.tar.gz", repos = NULL) # What version should it be? dendextend 1.3.0 requires whisker
install.packages("https://cran.r-project.org/src/contrib/Archive/dendextend/dendextend_1.3.0.tar.gz", repos = NULL) # What version should it be? d3heatmap 0.6.1.1 requires dendextend
remotes::install_version("d3heatmap", version="0.6.1.1", upgrade = "never", dependencies = F)
install.packages("https://cran.r-project.org/src/contrib/Archive/curl/curl_2.3.tar.gz", repos = NULL) # What version should it be? httr 1.2.1 requires curl
install.packages("https://cran.r-project.org/src/contrib/Archive/openssl/openssl_0.9.6.tar.gz", repos = NULL) # What version should it be? httr 1.2.1 requires openssl
remotes::install_version("httr", version="1.2.1", upgrade = "never", dependencies = F)
remotes::install_version("dplyr", version="0.7.1", upgrade = "never", dependencies = F)

#Bioconductor 3.5 is deprecated
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/limma_3.32.10.tar.gz", repos = NULL) # Instead of 3.32.2
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/edgeR_3.18.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/BiocGenerics_0.24.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/Biobase_2.36.2.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/S4Vectors_0.16.0.tar.gz", repos = NULL)
install.packages("https://cran.r-project.org/src/contrib/Archive/snow/snow_0.4-2.tar.gz", repos = NULL) # What version should it be? BiocParallel 1.10.1 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/futile.options/futile.options_1.0.0.tar.gz", repos = NULL) # What version should it be? futile.logger 1.4 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/lambda.r/lambda.r_1.1.9.tar.gz", repos = NULL) # What version should it be? futile.logger 1.4 requires it
install.packages("https://cran.r-project.org/src/contrib/Archive/futile.logger/futile.logger_1.4.tar.gz", repos = NULL) # What version should it be? BiocParallel 1.10.1 requires it
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/BiocParallel_1.10.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/IRanges_2.12.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/zlibbioc_1.22.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/XVector_0.16.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/data/annotation/src/contrib/GenomeInfoDbData_0.99.0.tar.gz", repos = NULL) 
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/GenomeInfoDb_1.14.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/GenomicRanges_1.30.3.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/DelayedArray_0.4.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/SummarizedExperiment_1.8.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/AnnotationDbi_1.38.2.tar.gz", repos = NULL) # Instead of 1.38.1
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/annotate_1.54.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/genefilter_1.58.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/geneplotter_1.54.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/DESeq2_1.16.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/BiocInstaller_1.28.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/Biostrings_2.44.2.tar.gz", repos = NULL) # Instead of 2.44.1
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/sva_3.26.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/affyio_1.46.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/GEOquery_2.42.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/preprocessCore_1.38.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/affy_1.54.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/affxparser_1.48.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/oligoClasses_1.38.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/oligo_1.40.2.tar.gz", repos = NULL) # Instead of 1.40.1
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/pcaMethods_1.68.0.tar.gz", repos = NULL) 
install.packages("https://www.bioconductor.org/packages/3.6/bioc/src/contrib/scde_2.6.0.tar.gz", repos = NULL)  # Instead of 2.5.0
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/tximport_1.4.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/biomaRt_2.32.1.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/rhdf5_2.20.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/scater_1.4.0.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/SC3_1.4.2.tar.gz", repos = NULL)
install.packages("https://www.bioconductor.org/packages/3.5/bioc/src/contrib/SCAN.UPC_2.18.0.tar.gz", repos = NULL)


# FAILS
#remotes::install_version("rPython", version="0.0-6", upgrade = "never", dependencies = F)
#install.packages("https://cran.r-project.org/src/contrib/Archive/rPython/rPython_0.0-6.tar.gz", repos = NULL)


# https://www.bioconductor.org/packages/3.5/bioc/html/

# NOT FOUND PACKAGES
#remotes::install_version("scLVM", version="0.99.3", upgrade = "never", dependencies = F)








