/*///////////
 *Programmed by: Nawal Velez and Komail Dharsee
 *Frienship Graph Algorithms
 *CS112 Fall 2011
 *
 */

package structures;
import java.io.*;
import java.util.*;

public class FriendGraph {
	public class Friend{
		String name;
		String school;
		Integer vnum;
		Friend next;
		public Friend(){
			name = null;
			school = "";
			vnum = null;
			next = null;
		}
	}
	public class node{
		node prev;
		Friend data;
		public node(node prev, Friend data){
			this.prev = prev;
			this.data = data;
		}
	}
	public FriendGraph(String docFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(docFile));
		String firstline = br.readLine();
		if (firstline == null){
			this.size = 0;
			return;
		}
		Integer size = new Integer(Integer.parseInt(firstline.trim()));
		this.size = size;
		names = new TreeMap<String, Friend>();
		vnums = new TreeMap<Integer, Friend>();
		adjLists = new ArrayList<Friend>(size);
		if (size == 0) return;
		for(int i = 0; i < (int)size; i++) adjLists.add(null);
		for(int i = 0; i < (int)size; i++){
			String line = br.readLine().trim();
			while (line.equals("")) line = br.readLine();
			Friend temp = new Friend();
			StringTokenizer st = new StringTokenizer(line, "|");
			String name = st.nextToken();
			temp.name = name;
			temp.vnum = i;
			temp.school = "";
			if (st.nextToken().equals("y"))	temp.school = st.nextToken();
			names.put(temp.name, temp);
			vnums.put(i, temp);
			
			temp.vnum = i;
			vnums.put(i, temp);
			names.put(temp.name, temp);
		}
		while(true){
			String line;
			try {
				line = br.readLine().trim();
				while(line.equals("")) line = br.readLine();
			} catch (NullPointerException e) {
				break;
			}
			StringTokenizer st = new StringTokenizer(line, "|");
			String person = st.nextToken();
			String f = st.nextToken();
			Integer personvnum, fvnum;
			personvnum= names.get(person).vnum;
			fvnum = names.get(f).vnum;
			Friend temp = new Friend();
			Friend temp2 = new Friend();
			temp = adjLists.get(personvnum);
			temp2 = adjLists.get(fvnum);
			Friend friendf = new Friend();
			Friend friendperson = new Friend();
			friendf.name = f;
			friendf.vnum = names.get(f).vnum;
			friendperson.name = person;
			friendperson.vnum = names.get(person).vnum;

			friendf.school = names.get(f).school;
			friendperson.school = names.get(person).school;
			friendf.next = temp;
			friendperson.next = temp2;
			adjLists.set(personvnum, friendf);
			adjLists.set(fvnum, friendperson);
		}
	}
	TreeMap<String, Friend> names;
	TreeMap<Integer, Friend> vnums;
	ArrayList<Friend> adjLists;
	public Integer size;
	public FriendGraph(){
		names = null;
		vnums = null;
		adjLists = null;
	}
	public FriendGraph subgraph(String school){
		FriendGraph result = new FriendGraph();
		result.names = new TreeMap<String, FriendGraph.Friend>();
		result.vnums = new TreeMap<Integer, FriendGraph.Friend>();
		result.adjLists = new ArrayList<FriendGraph.Friend>();
		result.size = new Integer(0);
		int count = 0;
		for (int i = 0; i < this.adjLists.size(); i++){
			if (vnums.get(i).school.equals(school)){
				Friend temp = new Friend();
				temp.name = vnums.get(i).name;
				temp.school = vnums.get(i).school;
				temp.vnum = count;
				result.names.put(temp.name, temp);
				result.vnums.put(temp.vnum, temp);
				result.size++;
				count++;
			}
		}
		for (int i = 0; i < result.vnums.size(); i++){
			Friend head = null;
			Friend headptr = head;
			Friend ptr = adjLists.get(names.get(result.vnums.get(i).name).vnum); // <--- UNBELIEVABLEY COMPLICATED
			// ptr is the first friend in the ith person in rutgers' friends list
			while (ptr != null){
				if(ptr.school.equals(school)){
					if(head == null){
						head = new Friend();
						head.name = ptr.name;
						head.school = school;
						head.vnum = result.names.get(ptr.name).vnum;
						headptr = head;
					}else{
						headptr.next = new Friend();
						headptr = headptr.next;
						headptr.name = ptr.name;
						headptr.school = school;
						headptr.vnum = result.names.get(ptr.name).vnum;
					}
				}
			ptr = ptr.next;
			}
			result.adjLists.add(i, head);
		}
		return result;
	}
	
	public ArrayList<String> shortestPath(String a, String b){
		if (!names.containsKey(a) || !names.containsKey(b)) return null;

		boolean found = false;
		ArrayList<node> result = new ArrayList<node>();
		boolean[] visited = new boolean[size];
		Stack<node> stk = new Stack<node>();
		Queue<node> q = new LinkedList<node>();
		//dequeue = remove
		// enqueue = add
		Integer temp = this.names.get(a).vnum;
		node A = new node(null, vnums.get(temp));
		q.add(A);
		visited[(int)temp] = true;
		while(!q.isEmpty()){
			node X = q.remove();
			stk.push(X);
			if (X.data.name.equals(b)){
				found = true;
				break;
			}
			for(Friend ptr = adjLists.get(X.data.vnum); ptr != null; ptr = ptr.next){
				if (!visited[ptr.vnum]){
					visited[ptr.vnum]=true;
					node qnode = new node(X, ptr);
					q.add(qnode);
				}
			}
		}
		if(!found) return null; 
		result.add(stk.pop());
		while(!stk.isEmpty()){
			node pop = stk.pop();
			//compare pop to result.get(0) and check if the data names
			int c = pop.data.name.compareTo(result.get(0).prev.data.name);
			if (c == 0) result.add(0, pop);	
		}
		ArrayList<String> finals = new ArrayList<String>(result.size());
		for (int i = 0; i < result.size(); i++){
			finals.add(i, result.get(i).data.name);
		}
		return finals;
	}
	public ArrayList<FriendGraph> clique(String school){
		FriendGraph sub = subgraph(school);
		ArrayList<FriendGraph> result = dfsDriver(sub);
		for (int i = 0; i < result.size(); i++){
			result.get(i).size = result.get(i).names.size();
		}
		return result;
		
	}
	private ArrayList<FriendGraph> dfsDriver(FriendGraph sub){
		boolean[] mastervisited = new boolean[(int)sub.size];
		ArrayList<FriendGraph> result = new ArrayList<FriendGraph>();
		for(int i = 0; i < sub.size; i++){
			boolean[] visited = new boolean[mastervisited.length];
			while (i < sub.size && mastervisited[i]) i++;
			if (i >= sub.size) break;
			visited = dfs(i, mastervisited, visited, sub);
			result.add(makesub(visited, sub));
		}
		
		
		return result;
	}
	private boolean[] dfs(Integer v,boolean[] mastervisited, boolean[] visited, FriendGraph sub){
		visited[v] = true;
		mastervisited[v] = true;
		for(Friend ptr = sub.adjLists.get(v); ptr != null; ptr = ptr.next){
			if(!mastervisited[ptr.vnum]){
				dfs(ptr.vnum, mastervisited, visited, sub);
			}
		}
		return visited;
	}
	private FriendGraph makesub(boolean[] visited, FriendGraph sub){
		//makes a subgraph of the given visited array using the nodes of which are true
		FriendGraph result = new FriendGraph();
		result.names = new TreeMap<String, Friend>();
		result.vnums = new TreeMap<Integer, Friend>();
		for(int i = 0; i < visited.length; i++){
			if (visited[i]){
				Friend temp = new Friend();
				temp.name = sub.vnums.get(i).name;
				temp.school = sub.vnums.get(i).school;
				temp.vnum = result.names.size();
				result.names.put(temp.name, temp);
				result.vnums.put(result.names.size()-1, temp);
				result.size = names.size();
			}
		}
		//now make the adjLists for the result graph
		result.adjLists = new ArrayList<Friend>();
		for(int i = 0; i < result.names.size(); i++){
			System.out.println(i);
			//add all the friends to each person., each of whom are from the same school as the person
			Friend ptr = adjLists.get(names.get(result.vnums.get(i).name).vnum);
			Friend head = null;
			Friend headptr = head;
			while (ptr != null){
				if(ptr.school.equals(result.vnums.get(i).school)){
					if(head == null){
						head = new Friend();
						head.name = ptr.name;
						head.school = result.vnums.get(i).school;
						head.vnum = result.names.get(ptr.name).vnum;
						headptr = head;
					}else{
						headptr.next = new Friend();
						headptr = headptr.next;
						headptr.name = ptr.name;
						headptr.school = result.vnums.get(i).school;
						headptr.vnum = result.names.get(ptr.name).vnum;
					}
				}
			ptr = ptr.next;
			}
			result.adjLists.add(i, head);
		}
		return result;
	}
	public ArrayList<String> connector(){
			if (this.size == 0) return null;
			int g = 0;
			for (Friend ptr = adjLists.get(0); ptr != null; ptr = ptr.next) g++;
			if (g >= size) return null;
			
			return null;
	}
	public void print(){
		System.out.println(size);
		if (size.equals(0)){
			System.out.println("No one goes to that school");
			return;
		}
		if (size == null) System.out.println("null");
		boolean[] checkprint = new boolean[size];
		for(int i = 0; i < size; i++){
			if (vnums.get(i).school.equals("")) System.out.println(vnums.get(i).name + "|n");
			else System.out.println(vnums.get(i).name + "|y|" + vnums.get(i).school);
		}
		for(int i = 0; i < adjLists.size(); i++){
			String person = vnums.get(i).name;
			for(Friend ptr = adjLists.get(i); ptr != null; ptr = ptr.next){
				if (!checkprint[names.get(ptr.name).vnum])
					System.out.println(person + "|" + ptr.name);
			}
			checkprint[i] = true;
		}
	}
}

