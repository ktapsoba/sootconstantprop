package resources;

import java.util.Objects;

public class Method {
	
	String className;
	String name;
	
	public Method(String cls, String n){
		className = cls;
		name = n;
	}
	
	public String toString(){
		return className + "." + name;
	}
	
	public boolean equals(Method method){
		return this.hashCode() == method.hashCode();
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(toString());
	}

}
