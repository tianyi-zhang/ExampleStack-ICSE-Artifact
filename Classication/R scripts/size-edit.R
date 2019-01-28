# change the path to the absolute path if the log file is not found
data1 <- read.csv(
  file="./log/correlation_ast_edit(adaptation).csv",
  header=FALSE, sep=",")
data2 <- read.csv(
  file="./log/correlation_ast_edit(variation).csv", 
  header=FALSE, sep=",")

# based on the distribution, trucate at the LOC of 100 to avoid sparse data
data_by_size_explicit <- data1[data1$V1<=100,]
data_by_size_potential <- data2[data2$V1<=100,]

# group the data by LOC and compute the median of AST edits in each LOC group
size_edit_explicit <- aggregate(data_by_size_explicit[, 4], list(data_by_size_explicit$V1), median)
size_edit_potential <- aggregate(data_by_size_potential[, 4], list(data_by_size_potential$V1), median)

size_explicit <- size_edit_explicit$Group.1
edit_explicit <- size_edit_explicit$x
size_potential <- size_edit_potential$Group.1
edit_potential <- size_edit_potential$x

par(mgp=c(4,1,0))
par(mar=c(6, 10, 1, 5))
plot(size_explicit, edit_explicit, xlab="Code Size (LOC)", 
     ylab="Number of AST edits (median)", col=c("blue"), cex.lab=1.8, cex.axis=1.8, pch = 1, ylim=c(0,200))
points(size_potential, edit_potential, col="red", pch = 4)
lines(loess.smooth(size_explicit,edit_explicit), col="blue", lwd=2)
lines(loess.smooth(size_potential,edit_potential), col="red", lwd=2)
legend("topright", legend = c("adaptation", "variation"), 
       col=c("blue", "red"), lty=NULL, cex=1.4, pch=c(1,4))
