This folder contains the artifacts involved in Section III and Section IV in ICSE Paper#401. First, we provide the manual inspection spreadsheet for developing the 24 adaptation categories. Second, we provide an automatic adaptation classification tool and the instructions to reproduce the evaluation result of this tool. Third, we provide the instructions to reproduce the type frequency analysis result using the automated classification tool.

1. Manual Inspection (Section III.A in the ICSE paper)

The Excel spreadsheet named "ManualInspection.xlsx" contains our manual inspection described in Section III(A) in the paper. If the file cannot be opened, please visit this google spreadsheet: https://bit.ly/2UhCDm5.

The first sheet "Manual analysis from adaptation dataset" contains 200 pairs randomly sampled from adaptation dataset. 
Column "StackOverflow link" contains the url to the SO example.
Column "GitHub link" contains the url to the GitHub file and method.
Column "AST edits #" contains the number of edits between the pair analyzed by GumTree.
Column "High-level Transformation" contains our manual description of the changes from SO to GitHub.

The second sheet "Manual analysis from variation dataset" contains 200 pairs sampled from variation dataset. The structure of the sheet is the same as the first one.

Based on the analysis from the two sheets, we summarized the 24 adaptation types and their corresponding rules in the third sheet, named "Summarized adaptation types". The content of this sheet is also presented in Table I in the paper. The rules are used to implement the automatic adaptation categorization tool introduced in Section III(B) in the paper. 

The fourth sheet "Test dataset" in the spreadsheet is the sampled and manually labeled 100 pairs used for testing the accuracy of our automatic adaptation categorization tool (Section III(C)).
Column "Manual Label" contains the adaptation types manually labeled.
Column "Inferred Labels" contains the results from the automatic tool.


2. Automated Adaptation Categorization (Section III.B and Section III.C in the ICSE paper)

The source code of our implementation of the automatic adaptation categorization tool is stored as examplestack-source.jar. A executable version can be found at examplestack-runnable.jar

For the purpose of demonstration, we provide our manually labeled 100 pairs for testing accuracy in Section III(C) in the paper with their original source codes as an example dataset. They are stored under folder "test-data". The clone groups are under subfolder "clones", the manually labeled adaptation types are in file "oracle/test/test-set.tsv"

Note: The output of the following programs are already available in "output-demo" folder. If you'd like to reproduce the results, please back them up somewhere else and create a new output folder.

Class: AdaptationSizeAndTypeAnalysis
Package: edu.ucla.cs.examplestack
Functionality: Run GumTree on each clone pair, generate AST edits info from GumTree, then automatically categorize adaptation types.
Command:
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationSizeAndTypeAnalysis [directory_to_clone_groups] [output_directory]
	By using our sample dataset, the command will be:
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationSizeAndTypeAnalysis test-data/clones output-demo
Output: output-demo/edits.csv, output-demo/adaptations.csv
	edits.csv contains Stack Overflow(SO) filename, GitHub filename, SO AST node count, GH 	AST node count, number of edits between this clone pair, and number of inserts between this clone pair.
	adaptations.csv contains SO filename, GitHub filename, and automatically generated adaptation types for this clone pair.


Class: AdaptationAnalysisEvaluator
Package: edu.ucla.cs.examplestack
Funtionality: Evaluate precision and recall of this automatic adaptation categorization tool
Command:
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationAnalysisEvaluator [directory_to_clone_groups] [directory_to_labeled_types]
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationAnalysisEvaluator test-data/clones test-data/oracle/test-labeled.tsv
Output: The following stats will be printed:
	Total Number of Clone Groups: 100
	Total Number of Clone Pairs: 100
	Completely Correctly Labeled Pairs: 80(80%)
	Partially Correctly Labeled Pairs: 20(20%)
	Total Number of Auto-labeled Adaptations: 440
	Total Number of Manual-labeled Adaptations: 449
	Precision of Labeling: 98%
	Recall of Labeling: 96%


Class: CalculateAdaptationTypeFrequency
Package: edu.ucla.cs.examplestack.stats
Functionality: Take the result of AdaptationSizeAndTypeAnalysis as input to calculate frequencies of adaptation types. The frequencies will be used for plotting Figure 3 in the paper.
Command:
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.stats.CalculateAdaptationTypeFrequency [directory_to_clone_groups] [result_of_adaptation_analysis]
	java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.stats.CalculateAdaptationTypeFrequency test-data/clones output-demo/adaptations.csv
Output: The following stats will be printed:
	Adaptation_type	Number_of_clone_groups_contain_this_type
	Change_Conditional_Expression	16
	Handle_New_Exception	4
	Declare_Unknown_Variable	1
	Change_Comment	33
	Change_Logging	15
	Change_Method_Call	78
	Change_Finally	1
	Insert_Delete_Try_Catch	7
	Update_Exception_Type	1
	Change_Access_Modifier	35
	Change_Annotation	21
	Inline_Field	7
	Rename	61
	Insert_If_Check	13
	Change_Catch_Block	4
	Change_Curly_Brace	7
	Insert_Final_Modifier	6
	Update_Constant_Value	51
	Delete_UnKnown_Variable_Or_Call	21
	Factor_Out_Constant	16
	Close_Stream	1
	Resolve_Local_Method_Call	1
	Insert_Delete_Thrown_Exception	4
	Update_Type	36


3. Reproduce data of the paper from adaptation dataset and variation dataset (Section IV in the ICSE paper)

3.1 Reproduce Figure 2 in Section IV in the ICSE paper

Go to the "R scripts" folder and open the three R files in R Studio. 

- edit-dist.R : reproduce the distribution of the number of AST edits to adapt Stack Overflow examples in the two datasets in Figure 2(a).

- size-edit.R : reproduce the correlation between the code length of a Stack Overflow example and the number of AST edits required to adapt it in the two datasets in Figure 2(b).

- vote-edit.R : reproduce the correlation between the vote score of a Stack Overflow example and the number of AST edits required to adapt it in the two datasets in Figure 2(c).

If R Studio reports file-not-found errors, please change the log file paths to absolute paths in your machine.  

3.2 Reproduce Figure 3 in Section IV in th ICSE paper

Figure 3 is generated from the Excel sheet, adaptation-type-frequency.xlsx.

We also provide a way to reproduce the type frequency numbers in the Excel sheet, if you are interested. Simly run the AdaptationSizeAndTypeAnalysis from our examplestack-runnable.jar file using the following commands.

# reproduce the type frequency on the adaptation dataset
java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationSizeAndTypeAnalysis ../Datasets/adaptation-dataset output-adaptation-dataset
java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.stats.CalculateAdaptationTypeFrequency ../Datasets/adaptation-dataset output-adaptation-dataset/adaptations.csv

# reproduce the type frequency on the variation dataset
java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.AdaptationSizeAndTypeAnalysis ../Datasets/variation-dataset output-variation-dataset
java -cp examplestack-runnable.jar edu.ucla.cs.examplestack.stats.CalculateAdaptationTypeFrequency ../Datasets/variation-dataset output-variation-dataset/adaptations.csv

Note: The output on the two datasets are already available in the folders "output-adaptation-dataset" and "output-variation-dataset" respectively. If you'd like to reproduce the results, please back them up somewhere else and create a new output folder.

For a desktop machine with 1.7 GHz Intel Core i7 and 8G RAM, it takes approximately 1 hour to run AdaptationSizeAndTypeAnalysis on variation dataset.  


Reference
H. Sajnani, V. Saini, J. Svajlenko, C. K. Roy, and C. V. Lopes, “Sourcerercc: Scaling code clone detection to big-code,” in Software Engineering (ICSE), 2016 IEEE/ACM 38th International Conference on. IEEE, 2016, pp. 1157–1168. 
