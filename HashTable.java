/* HashTable.java
   CSC 225 - Spring 2016
   Template for string hashing
   
   =================
   
   Modify the code below to use quadratic probing to resolve collisions.
   
   Your task is implement the insert, find, remove, and resize methods for the hash table.
   
   The load factor should always remain in the range [0.25,0.75].
   
   =================
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java HashTable
	
   Input data should consist of a list of strings to insert into the hash table, one per line,
   followed by the token "###" on a line by itself, followed by a list of strings to search for,
   one per line.
	
   To conveniently test the algorithm with a large input, create
   a text file containing the input data and run the program with
	java HashTable file.txt
   where file.txt is replaced by the name of the text file.

   B. Bird - 07/04/2015
   M. Simpson - 21/02/2016
*/
/* Student author: Dahv Reinhart - V00735279
 * Author of insert(), remove(), find(), resize(), checkPrime()
 * Program completed for CSC225 Assignment 3 - Due 03/11/2016
 */

import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import java.lang.Math;

public class HashTable{

     //The size of the hash table.
     int TableSize;
     
     //The current number of elements in the hash table.
     int NumElements;
	
	//The variable T represents the array used for the table.
	String[] T;
	
	public HashTable(){
     	NumElements = 0;
     	TableSize = 997;
     	T = new String[TableSize];
     	for(int i = 0; i < TableSize; i++) { //init the table to be all null values
     		T[i] = null;
     	}
	}
	
	/* hash(s) = ((2^0)*s[0] + (2^1)*s[1] + (2^2)*s[2] + ... + (2^(k-1))*s[k-1]) mod TableSize
	   (where s is a string, k is the length of s and s[i] is the i^th character of s)
	   Return the hash code for the provided string.
	   The returned value is in the range [0,TableSize-1].
	*/
	public int hash(String s){
		int h = 0;
     	for (int i=0; i<s.length(); i++) {
           	h += s.charAt(i) * Math.pow(2,i);
     	}
		return h % TableSize;
	}
	
	/* insert(s)
	   Insert the value s into the hash table and return the index at 
	   which it was stored.
	*/
	public int insert(String s){

		//if find() says that the value is present, then no need to insert a 2nd copy
		if(find(s) != -1) {
			return -1;
		}

		//Check to see if the load factor is acceptable - if not, upsize the table
		if((1.0*(NumElements+1)/TableSize) > 0.75) {
			resize(TableSize*2);
		}

		//set the quadratic base, the initial hash value and the initial index value
		int base = 0;
		int hashValue = hash(s);
		int index = hashValue;

		//check if the initial index is viable, 
		//if a collision occures, probe until a suitable index is found
		while(T[index] != null) {
			base++;
			index = (hashValue + (base*base)) % TableSize;
		}

		//insert the new string
		T[index] = s;
		NumElements++;

		return index;
	}
	
	/* find(s)
	   Search for the string s in the hash table. If s is found, return
	   the index at which it was found. If s is not found, return -1.
	*/
	public int find(String s){

		//Simply go through the entire HashTable and see if a particular value is present
     	for(int i = 0; i < TableSize; i++) {
			if(T[i] != null) {
				if(T[i].equals(s)) {
					return i; //return the index where the correct value is located
				}
			}
		}
     	return -1; //if not value not present in the table, return -1
	}
	
	/* remove(s)
	   Remove the value s from the hash table if it is present. If s was removed, 
	   return the index at which it was removed from. If s was not removed, return -1.
	*/
	public int remove(String s){

		//Check to see if the load factor is acceptable - if not, downsize the table
     	if((1.0*(NumElements-1)/TableSize) < 0.25) {
			resize(TableSize/2);
		}

		int index = find(s); //locate the string to be removed

		if (index == -1) {
			//Value not present in the HashTable -- unable to be removed!
			return -1;
		}

		//Otherwise, value is present so simply remove it from the table
		T[index] = null;
		NumElements--;

     	return index; //return the index of the removed value
	}
	
	/* resize()
	   Resize the hash table to be within the load factor requirements.
	*/
	public void resize(int newSize){
		
		if(newSize < 23) {
			//reasonable minimum size
			newSize = 23;
		}
		else {
			//otherwise, ensure that the provided size is a prime number
			while(checkPrime(newSize) == false) {
				newSize++;
			}
		}

		if(newSize == TableSize) {
			//calls to remove() have been made successivle while the load factor is low
			//since we have a minimum table size, no use doing an entire resize to the same size
			//the resize function takes a lot of horsepower, so this saves time
			return;
		}

		//copy all strings into temp array
		String[] temp = new String[TableSize];
		for(int i = 0; i < TableSize; i++) {
			if(T[i] != null) {
				temp[i] = T[i];
			}
		}

		//increase or decrease size and re-initialize the table to null values
		TableSize = newSize;
		T = new String[TableSize];
		for(int i = 0; i < TableSize; i++) {
     		T[i] = null;
     	}
		NumElements = 0; //reset NumElements to ensure an accurate count

		//add all the past strings back into the new table
		for(int i = 0; i < temp.length; i++) {
			if (temp[i] != null) {
				insert(temp[i]);
			}
		}
	}

	// checkPrime() takes in a supplied integer and checks if it is prime or not
	public boolean checkPrime(int num) {

		if(num%2 == 0) {
			return false;
		}
		for(int i = 3; i <= (num/2); i+=2) {
			if (num%i == 0) {
				return false;
			}
		}

		return true;
	}
	
	/* **************************************************** */
	
	/* main()
	   Contains code to test the hash table methods. 
	*/
	public static void main(String[] args){
		Scanner s;
		boolean interactiveMode = false;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			interactiveMode = true;
			s = new Scanner(System.in);
		}
		s.useDelimiter("\n");
		if (interactiveMode){
			System.out.printf("Enter a list of strings to store in the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading table values from %s.\n",args[0]);
		}
		
		Vector<String> tableValues = new Vector<String>();
		Vector<String> searchValues = new Vector<String>();
		Vector<String> removeValues = new Vector<String>();
		
		String nextWord;
		
		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			tableValues.add(nextWord);
		System.out.printf("Read %d strings.\n",tableValues.size());
		
		if (interactiveMode){
			System.out.printf("Enter a list of strings to search for in the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading search values from %s.\n",args[0]);
		}	
		
		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			searchValues.add(nextWord);
		System.out.printf("Read %d strings.\n",searchValues.size());
		
		if (interactiveMode){
			System.out.printf("Enter a list of strings to remove from the hash table, one per line.\n");
			System.out.printf("To end the list, enter '###'.\n");
		}else{
			System.out.printf("Reading remove values from %s.\n",args[0]);
		}
		
		while(s.hasNext() && !(nextWord = s.next().trim()).equals("###"))
			removeValues.add(nextWord);
		System.out.printf("Read %d strings.\n",removeValues.size());
		
		HashTable H = new HashTable();
		long startTime, endTime;
		double totalTimeSeconds;
		
		startTime = System.currentTimeMillis();

		for(int i = 0; i < tableValues.size(); i++){
			String tableElement = tableValues.get(i);
			long index = H.insert(tableElement);
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;
		
		System.out.printf("Inserted %d elements.\n Total Time (seconds): %.2f\n",tableValues.size(),totalTimeSeconds);
		
		int foundCount = 0;
		int notFoundCount = 0;
		startTime = System.currentTimeMillis();

		for(int i = 0; i < searchValues.size(); i++){
			String searchElement = searchValues.get(i);
			long index = H.find(searchElement);
			if (index == -1)
				notFoundCount++;
			else
				foundCount++;
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;
		
		System.out.printf("Searched for %d items (%d found, %d not found).\n Total Time (seconds): %.2f\n",
							searchValues.size(),foundCount,notFoundCount,totalTimeSeconds);
							
		int removedCount = 0;
		int notRemovedCount = 0;
		startTime = System.currentTimeMillis();

		for(int i = 0; i < removeValues.size(); i++){
			String removeElement = removeValues.get(i);
			long index = H.remove(removeElement);
			if (index == -1)
				notRemovedCount++;
			else
				removedCount++;
		}
		endTime = System.currentTimeMillis();
		totalTimeSeconds = (endTime-startTime)/1000.0;
		
		System.out.printf("Tried to remove %d items (%d removed, %d not removed).\n Total Time (seconds): %.2f\n",
							removeValues.size(),removedCount,notRemovedCount,totalTimeSeconds);
	}
}
