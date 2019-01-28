# change the path to the absolute path if the log file is not found
data1 <- read.csv(
  file="./lof/correlation_ast_edit(adaptation).csv",
  header=FALSE, sep=",")
data2 <- read.csv(
  file="./log/correlation_ast_edit(variation).csv", 
  header=FALSE, sep=",")

# based on the distribution, trucate at the score of 200 to avoid sparse data
data_by_score_explicit <- data1[data1$V3<=400,]
data_by_score_potential <- data2[data2$V3<=400,]

score_edit_explicit <- aggregate(data_by_score_explicit[, 4], list(data_by_score_explicit$V3), median)
score_edit_potential <- aggregate(data_by_score_potential[, 4], list(data_by_score_potential$V3), median)

score_explicit <- score_edit_explicit$Group.1
score_potential <- score_edit_potential$Group.1

edit_explicit <- score_edit_explicit$x
edit_potential <- score_edit_potential$x

par(mgp=c(4,1,0))
par(mar=c(6, 10, 1, 5))
plot(score_explicit, edit_explicit, xlab="Vote Score (i.e., upvotes minus downvotes)", 
     ylab="Number of AST edits (median)", col=c("blue"), cex.lab=1.8, cex.axis=1.8, pch = 1, ylim=c(0, 100))
points(score_potential, edit_potential, col="red", pch = 4)
lines(loess.smooth(score_explicit,edit_explicit), col="blue", lwd=2)
lines(loess.smooth(score_potential,edit_potential), col="red", lwd=2)
legend("topright", legend = c("adaptation", "variation"), 
       col=c("blue", "red"), lty=NULL, cex=1.4, pch=c(1,4))