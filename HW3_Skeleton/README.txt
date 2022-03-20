									 loai abu yosef		
		


to implement a program that builds a classifcation tree i have used several helper methods beside the main methods.

helper functions : 

1.majorlabel method that returns the most appreared label from the given set,we use this method to decide which label to give the new builded node in the tree.
2.labelsisequal method that returns true if all labels are the same from the given set otherwise returns false.
3.entropy method to compute the entropy of the tree , we need the entropy value for computing the info gain of the tree.
4.averageentropy method to compute the conditional entropy for all possible pairs of attributes of the tree , we need the conditional entropy to compute the the information gain of the tree.

main functions :
*described on decisiontreeimpl.java file.

main data structures:

1.list for labels
2.list for attributes
3.list for attribute values
4.list for instances

**there is also a helper lists and arrays to compute entropy and conditional entropy and information gain.


******************* only the DecisionTreeImpl.java file have been implemented and modifed, other files are external files from the university***********************


