import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    
    root = decisionTreeLearning(train.instances, attributes, majorLabel(train.instances), null);
  }

  /*private method to build a decision tree recursively by learning each instance that given in the data set */
  private DecTreeNode decisionTreeLearning(List<Instance> instances, List<String> attributes, String defLabel, String parentAttributeValue){
	  DecTreeNode node = null;
	  int i = 0 , attrLength = attributes.size();
	  int size, maxGainIndex = 0;
	  double gain = entropy(instances) - averageEntropy(instances,attributes.get(i));
	  double Gain[] = new double[attrLength];
	  
	  Gain[i] = gain;
	  if (instances.isEmpty()) {
		  return node = new DecTreeNode(defLabel, null, parentAttributeValue, true);
	  }
	  if (labelsIsEqual(instances)) {
		  return node = new DecTreeNode(instances.get(0).label, null, parentAttributeValue, true);
	  }
	  if (attributes.isEmpty()) {
		  return node = new DecTreeNode(majorLabel(instances), null, parentAttributeValue, true);
	  } 
	  for (i = 1; i < attrLength; i++) {
		  gain = entropy(instances) - averageEntropy(instances,attributes.get(i));
		  Gain[i] = gain;
	  }
	 
	  double maxGain = 0;
	  i = 0;  
	  while(i++ < attrLength) {
		  if (maxGain < Gain[i]) {
			  maxGain = Gain[i];
			  maxGainIndex = i;
		  }
	  }
	  
	  String maxAttribute = attributes.get(maxGainIndex);
	  node = new DecTreeNode(majorLabel(instances), maxAttribute, parentAttributeValue, false);
	  List<String> thisAttribute = new ArrayList<String>(attributes);
	  thisAttribute.remove(maxGainIndex);
	  size = attributeValues.get(maxAttribute).size();
	  int j = 0;
	  while(j++ < size) {
		  List<Instance> tempInstance = new ArrayList<Instance>();
		  for (i = 0; i < instances.size(); i++) {
			  if (instances.get(i).attributes.get(getAttributeIndex(maxAttribute)).equals(attributeValues.get(maxAttribute).get(j))) {
				  tempInstance.add(instances.get(i));
			  }
		  }
		  DecTreeNode child = decisionTreeLearning(tempInstance, thisAttribute, majorLabel(instances), attributeValues.get(maxAttribute).get(j));
		  node.addChild(child);
	  }
	  return node;	    
  }

  @Override
  /*method to classify a label to an given instance by forward checking from root to all child's of the root*/
  public String classify(Instance instance) {
	 int i = 0;
	 DecTreeNode node = root;
	 String attrValue = instance.attributes.get(getAttributeIndex(node.attribute));
	  while(!node.terminal) {
		  while(i < node.children.size()) {
			  if(attrValue.equals(node.children.get(i).parentAttributeValue)) {
				  node = node.children.get(i);
				  attrValue = instance.attributes.get(getAttributeIndex(node.attribute)); 
				  break;
			  }
			  i++;
		  }
	  }
	  return node.label;
  }

  /*method to print the information gain of the tree for the given train*/
  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    for (int i = 0; i < train.attributes.size(); i++)
    	System.out.format("%s %.5f\n", train.attributes.get(i), entropy(train.instances) - 
    			averageEntropy(train.instances, train.attributes.get(i)));
  }
  
  /*private method to compute the conditional entropy for the given attribute */
  private double averageEntropy(List<Instance> instances, String attribute) {
	  String firstLabel = instances.get(0).label;
	  String secondLabel = null;
	  int i = 1;
	  int size = instances.size();
	  while(i < size) {
		  if(!firstLabel.equals(instances.get(i).label)) {
			  secondLabel = instances.get(i).label;
			  break;
		  }
	  }
	  
	  int difAttrVals = attributeValues.get(attribute).size();
	  int attrIndex = getAttributeIndex(attribute);
	  int total[] = new int[difAttrVals],
	  firstLength[] = new int[difAttrVals],
	  secondLength[] = new int[difAttrVals]; 
	  double _condEntropy[] = new double[difAttrVals];
	  double condEntropy = 0, firstLabelSum = 0, secondLabelSum = 0;
	  i = 0;
	  
	  while(i++ < difAttrVals) {
		  total[i] = 0;
		  firstLength[i] = 0;
		  secondLength[i] = 0;
	  }
	  
	  for (i = 0; i < size; i++) {
		  for(int j = 0; j < difAttrVals; j++) {
			  if (instances.get(i).attributes.get(attrIndex) == attributeValues.get(attribute).get(j)) {
				  total[j]++;
				  if (instances.get(i).label.equals(firstLabel)) {
					  firstLength[j]++;
				  }
				  else if(instances.get(i).label.equals(secondLabel)) {
					  secondLength[j]++;
				  }
			  }
		  }
	  }
	  
	  for (i = 0; i < difAttrVals; i++) {
		  if (firstLength[i] == 0) {
			  firstLabelSum = 0;
		  }
		  
		  else if (firstLength[i] != 0) {
			  firstLabelSum = (double)-firstLength[i]/total[i] * Math.log((double)firstLength[i]/total[i])/Math.log(2);
		  }
		  
		  if (secondLength[i] == 0) {
			  secondLabelSum = 0;
		  }
		  
		  else if (secondLength[i] != 0) {
			  secondLabelSum = (double)-secondLength[i]/total[i] * Math.log((double)secondLength[i]/total[i])/Math.log(2);
		  }
		  
		  _condEntropy[i] = (double)total[i]/size * (firstLabelSum + secondLabelSum);
		  condEntropy = condEntropy + _condEntropy[i];  
	  }
      return condEntropy;
  }

  /*private method to compute the entropy of the builded decision tree*/
  private double entropy(List<Instance> instances) {
	  int i=1;
	  int numOfInstances = instances.size();
	  int firstLabelCount, secondLabelCount;
	  String label = instances.get(0).label;
	  firstLabelCount = 1;
	  secondLabelCount = 0;
	  
	  while(i++<numOfInstances) {
		  if (instances.get(i).label.equals(label))
			  firstLabelCount++;
		  else
			  secondLabelCount++;
	  }
	  
	  /*the probability of appearances of the first label in all instances */
	  double firstProbability = firstLabelCount/numOfInstances;
	 
	  /*the probability of appearances of the second label in all instances */
	  double secondProbability = secondLabelCount/numOfInstances;
	  
      return (-(firstProbability * Math.log(firstProbability) / Math.log(2)) 
    		 - (secondProbability * Math.log(secondProbability) / Math.log(2)));
  }

  @Override
  /*method to print the sum of matched labels from all instances that given in the test ,
   * if the given label of instance in test matches the label that classified to the instance matched labels increased by 1.*/
  public void printAccuracy(DataSet test) {
	  int i = 0, matchedLabels = 0;
		while(i++ < test.instances.size()) {
			if (test.instances.get(i).label.equals(classify(test.instances.get(i)))) {
				matchedLabels++;
			}
		}
		System.out.format("%.5f\n", (double)matchedLabels/test.instances.size());
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {
    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  /*boolean method to check if all labels of all instances are the same*/
  public boolean labelsIsEqual(List<Instance> instances) {
	  int i = 0;
	  boolean flag = true;
	  /*a while loop that breaks if we found an instance label different*/
	  while(i<instances.size()-1) {
		  if (!instances.get(i).label.equals(instances.get(i + 1).label)) {
			  flag = !flag;
			  break;
		  } 
		  i++;
	  } 
	  if (flag)
		  return true;
	  else
		  return false;  
  }
  
  /*private method that checks the label for each instance in instances list to choose the most appeared label */
  private String majorLabel(List<Instance> instances) {	
	  int i = 0, firstLabelCount = 0, secondLabelCount = 0;
	  String firstLabel = labels.get(0);
	  String secondLabel = labels.get(1); 
	  while(i<instances.size()) {
		  if (instances.get(i).label.equals(firstLabel))
			  firstLabelCount++;
		  else
			  secondLabelCount++;
		  i++;
	  }
	
	  if (firstLabelCount >= secondLabelCount)
		  return firstLabel;
	  else
		  return secondLabel;
	}
}
