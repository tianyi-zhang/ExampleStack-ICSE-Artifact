This folder contains two source datasets (downloaded GitHub repositories and SO code examples) and two generated datasets (variation dataset and adaptation datatset) presented in Section II in ICSE Paper#401.  

Please first download the datasets at https://figshare.com/s/299da5ec6d7ab0ee1878 (DOI: 10.6084/m9.figshare.7722068).The datasets can also be downloaded from Dropbox link https://bit.ly/2WmsztO. Unzip the two zip files.

Note that the variation dataset is 1.3G before unzip and 4.8G after unzip. Make sure you have enough disk space before you unzip it.


1. Downloaded GitHub repositories and processed SO code examples (Section II in the ICSE paper)
We downloaded 50,826 non-forked Java repositories with at least five stars. All urls of the repositories are provided in file "github-java-5-star-url.txt".
We extracted the code snippets from SO answer posts whose corresponding question posts have "java" or "android" tags. Then we wrapped the snippets as Java methods and kept those ones with at least 50 tokens. The processed SO code examples are presented in file "so-more-than-50-tokens.txt".


2. Adaptation dataset and variation dataset (Section II in the ICSE paper)

2.1 Variation dataset

To get the variation dataset, as described in Section II of the paper, we start from getting similar method pairs between Stack Overflow (SO) posts and GitHub Java projects by using SourcererCC[1]. Then a timestamp analysis is conducted to eliminate the pairs where the GitHub file is committed before the SO post. As a result, we got 14,124 clone groups, i.e. SO examples with its GitHub counterparts. The structure of the variation dataset is as follows, using the first group as an example:

- so-0
  -- so-15718991-10-1.java
  -- carved-gh-0-1-162-183.java
  -- gh-0-1-162-183.txt
  -- urls.txt
  -- timestamps.txt

Each folder represents a clone group, with 5 kinds of files inside:
  (1) SO method
  (2) Similar GitHub method(s) detected by SourcereCC. One SO method can have multiple GitHub clones.
  (3) The full GitHub file(s) containing the GitHub method(s).
  (4) Urls of the GitHub file(s).
  (5) Timestamps of the GitHub file(s).

2.2 Adaptation dataset

From the variation dataset, we screened GitHub clones with explicit references (SO links) to the SO post. The adaptation dataset consists of the 629 groups with explicit reference.
The structure of the adaptation dataset is the same as the variation dataset, except that there's no timestamp info involved.


3. Reproduce Figure 1 in Section II in the ICSE paper

Go to the "R scripts" folder and open the three R files in R Studio. 

- clone-dist.R : reproduce the distribution of Stack Overflow code examples with different numbers of GitHub clones in the two datasets in Figure 1(a).

- size-dist.R : reproduce the distribution of Stack Overflow code examples with different lines of code in the two datasets in Figure 1(b).

- vote-dist.R : reproduce the distribution of Stack Overflow code examples with different vote scores in the two datasets in Figure 1(c).

If R Studio reports file-not-found errors, please change the log file paths to absolute paths in your machine.  
