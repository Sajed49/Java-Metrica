package objectOrientedAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

class ClassInfo {
	
	int totalMethods = 0; //done
	ArrayList<String> childs = new ArrayList<String>(); //done
	
	String parentClass = ""; //done
	String filePath = null; //done
	String className = null; //done
	int depthOfInheritence;
	int totalChilds = 0; //done
	int noOfFields = 0;
	ArrayList<String> fields = new ArrayList<String>();
	
	public ClassInfo(String filePath) {
		
		this.filePath = filePath;
		findMethodCount();
		findClassNameAndParentClass();
		collectFields(new File(filePath));
		System.out.println(fields);

	}
	
	void findMethodCount() {	
		try {
            new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        //System.out.println(n.getNameAsString());
                        totalMethods++;
                    }
                }.visit(JavaParser.parse(new File(filePath) ), null);
                //System.out.println(); // empty line
            } catch (IOException e) {
                new RuntimeException(e);
        }
	}
	
	void collectFields(File file) {
		try {
            new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(FieldDeclaration n, Object arg) {
                        super.visit(n, arg);
                        for(VariableDeclarator v:n.getVariables()) {
                        	if(v.toString().contains("=")) {
                        		String[] voo = v.toString().split("=");
                				fields.add(voo[0]);
                        	}
                        	else fields.add(v.toString());
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
        }	
	}
	
	
	

	void findClassNameAndParentClass() {
		
		try {
            new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        //System.out.println(n.getNameAsString());
                        if( n.isInterface() == false ) {
                        	
                        	className = n.getNameAsString();
                        	//System.out.println(className);
                    	    List<String> list = Arrays.asList(n.getRange().toString().replaceAll("[^0-9]+", " ").trim().split(" "));
                        	findParentClassName(filePath, Integer.parseInt(list.get(0)));
                        	
                        }
                    }
                }.visit(JavaParser.parse(new File(filePath) ), null);
                //System.out.println(); // empty line
            } catch (IOException e) {
                new RuntimeException(e);
            }
	}
	
	
	void findParentClassName(String path, int lineNumber) {
		
		BufferedReader br = null;
		FileReader fr = null;
		String classDeclearationLine = null;
		int counter = 0;
		try {

			fr = new FileReader(path);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ( (sCurrentLine = br.readLine()) != null ) {
				
				counter++;
				if(counter == lineNumber) {
					classDeclearationLine = sCurrentLine;
					break;
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		List<String> list = Arrays.asList(classDeclearationLine.split("\\W+"));
		
		for(int i=0; i<list.size(); i++) {
			//System.out.println(list.get(i));
			if(list.get(i).equals("extends")) {
				parentClass = list.get(i+1);
				break;
			}
		}
	}
}
