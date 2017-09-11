cd bin
javac -d . ../src/*.java
jar -cvfm bayes.jar BayesManifest.txt  *.class

train=${2:-'../data/lymph_train.arff'}
test=${3:-'../data/lymph_test.arff'}

java -jar bayes.jar $train $test $1
cd ..
