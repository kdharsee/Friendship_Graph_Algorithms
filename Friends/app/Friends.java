package app;
import java.util.*;
import java.io.*;
import structures.*;
import structures.FriendGraph.node;
public class Friends {
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws IOException
	{
	
		System.out.print("Enter text file name to read: ");
		String infile = keyboard.readLine();
		FriendGraph treeMap = new FriendGraph(infile);
		while(treeMap.size == 0){
			System.out.println("Empty Graph, please enter a new file or exit(q)");
			infile = keyboard.readLine();
			if (infile.equals("q")) return;
			treeMap = new FriendGraph(infile);
		}
		
	
		menu(treeMap);
	}
	
	static void menu(FriendGraph vertex) throws IOException
	{
		String choices = "1 2 3 4 5";
		int num = 0;
		while(num != 5)
		{
			System.out.println("What would you like to do?: ");
			System.out.println("1. Students At School");
			System.out.println("2. Shortest Intro Chain");
			System.out.println("3. Cliques At School");
			System.out.println("4. Connectors");
			System.out.println("5. Exit.");
			String n = keyboard.readLine();
			while(!choices.contains(n))
			{
				System.out.println("That is not a valid choice, enter 1-5: ");
				n = keyboard.readLine();
			}
			num = Integer.parseInt(n);
			switch (num) 
			{
				case 1:
					System.out.println("Name of School?");
					String school = keyboard.readLine();
					FriendGraph run = vertex.subgraph(school);
					run.print();
					break;
				case 2:
					System.out.println("Name of the person who wants the intro?");
					String introname = keyboard.readLine();
					System.out.println("Name of the other person");
					String othername = keyboard.readLine();
					ArrayList<String> result = vertex.shortestPath(introname, othername);
					if (result == null) System.out.println("No path between these two people");
					else{
						for(int i = 0; i < result.size()-1; i++)
							System.out.print(result.get(i) + "-");
						System.out.println(result.get(result.size()-1));
					}
					break;
				case 3:
					System.out.println("Name of School which cliques are to be found?");
					String cliqueschool = keyboard.readLine();
					ArrayList<FriendGraph> result1 = vertex.clique(cliqueschool);
					if (result1.size() == 0){
						System.out.println("No cliques");
						break;
					}
					for(int i = 0; i <result1.size(); i++)
						result1.get(i).print();
					break;
				case 4:
					ArrayList<String> connector = vertex.connector();
					if (connector == null) System.out.println("no connectors");
					
					break;
				case 5:
					break;
				default: break;
			}
		}
	}
}