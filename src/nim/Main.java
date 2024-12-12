package nim;

public class Main {
	public static void main(String[] args) {
		   NimModel model = new NimModel();
	        NimView view = new NimView();
	        NimController controller = new NimController(model, view);
	        controller.run();
	}

}
