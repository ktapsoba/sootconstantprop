package resource;

public class Action {
	Action(){}
	
	static boolean isValidAction(StateType stateType, Method method){
		if (stateType instanceof Connected){
			return isValidAction((Connected)stateType, method );
		}
		else if (stateType instanceof NotConnected) {
			return isValidAction((NotConnected)stateType, method);
		}
		else if (stateType instanceof Statement){
			return isValidAction((Statement)stateType, method);
		}
		else if (stateType instanceof Result){
			return isValidAction((Result)stateType, method);
		}
		else if (stateType instanceof LoggedIn){
			return isValidAction((LoggedIn)stateType, method);
		}
		else if (stateType instanceof LoggedOut){
			return isValidAction((LoggedOut)stateType, method);
		}
		return false;
	}
	
	private static boolean isValidAction(Connected stateType, Method method){
		//rules for JDBC
		if(method.isGetConnection() || method.isCreateStatement() || method.isCloseConnection()){
			return true;
		}
		//rules for FTPClient
		else if( method.isDisconnect() || method.isLogin() || method.isConnect()){
			return true;
		}
		return false;
	}
	
	private static boolean isValidAction(NotConnected stateType, Method method){
		//rules for JDBC
		if (method.isGetConnection() || method.isCloseConnection() || method.isCloseResult() || method.isCloseStatement()){
			return true;
		}
		//rules for FTPClient
		if (method.isDisconnect() || method.isConnect()){
			return true;
		}
		return false;
	}
	
	private static boolean isValidAction(Statement stateType, Method method){
		if (method.isCloseStatement() || method.isCreateStatement() || method.isExecuteQuery()){
			return true;
		}
		return false;
	}
	
	private static boolean isValidAction(Result stateType, Method method){
		if(method.isCloseResult())
			return true;
		return false;
	}
	
	//ONLY FTPClient specific
	private static boolean isValidAction(LoggedIn stateType, Method method){
		if(method.isLogin() || method.isDisconnect() || method.isLogout()){
			return true;
		}
		return false;
	}
	
	private static boolean isValidAction(LoggedOut stateType, Method method){
		if (method.isLogin() || method.isDisconnect()){
			return true;
		}
		return false;
	}
}
