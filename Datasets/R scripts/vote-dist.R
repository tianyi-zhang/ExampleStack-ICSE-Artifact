# change the path to the absolute path if the log file is not found
data1 <- read.csv(file="./log/correlation_ast_edit(adaptation).csv",
                  header=FALSE, sep=",")
data2 <- read.csv(file="./log/correlation_ast_edit(variation).csv", 
                  header=FALSE, sep=",")

# show distribution of AST edits in both datasets
p1 = hist(data1$V3, breaks=40, xlim=c(0,400))
p1$density = p1$counts/sum(p1$counts)*100

p2 = hist(data2$V3, breaks=100, xlim=c(0,400))
p2$density = p2$counts/sum(p2$counts)*100

par(mgp=c(4,1,0))
par(mar=c(6, 10, 1, 5))
plot(p1, freq=FALSE, main="", xlab = "Vote Score (i.e., upvotes minus downvotes)", ylab = "Percentage of SO examples", 
     col = rgb(179,203,247,120,NULL, 255),
     cex.axis=1.8, cex.lab=1.8, xlim=c(0,400), ylim = c(0, 60))
plot(p2, freq=FALSE, main="", xlab = "Code Size (LOC)", ylab = "Percentage of SO examples", 
     col = rgb(243, 191, 184, 120, NULL, 255),  
     cex.axis=1.8, cex.lab=1.8, xlim=c(0,400), ylim=c(0, 10), add=TRUE)
legend("topright", legend = c("adaptation", "variation"), 
       col=c("blue", "red"), lty=1:1, cex=1.4)
