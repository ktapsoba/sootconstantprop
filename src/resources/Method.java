package resources;

import java.util.Objects;

public class Method {
	
	private String className;
	private String name;
	
	public Method(String cls, String n){
		className = cls;
		name = n;
	}
	
	public String toString(){
		return className + "." + name;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Method method){
		return this.hashCode() == method.hashCode();
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(toString());
	}

}
