#! /bin/sh

cd build/classes

rm testClassifieur

java batch.Batch -d ../../dictionnaire1000en.txt -as ../../baseapp/spam 250 -ah ../../baseapp/ham 100 -s testClassifieur

java batch.Batch -c testClassifieur -f FAST ../../basetest/ham/0.txt HAM

java batch.Batch -c testClassifieur -th ../../basetest/ham 100 -ts ../../basetest/spam 100
