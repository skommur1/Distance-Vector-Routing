package com;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.net.InetAddress;
import com.Vector;
import java.net.DatagramPacket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class Router extends Thread{
	
	public static InetAddress IPAddress;
	public static int[] neighboringPorts;
	public static String[] neighboringNodes;
	public static ArrayList<Vector> distanceVector;
	public static int noOfNeighbors;
	public static DatagramSocket dSocket;
	public static Date lastModified;
	public static File datFile;
	public static String fileName;
	public static String myName;
	public String type;
	
	Router(){
		
	}
	Router(String type){
		this.type = type;
	}
	
	public void run() {
		
			if(type.equalsIgnoreCase("s")) {
				this.sendVector();
			}
			if(type.equalsIgnoreCase("r")) {
				this.receiveVector();
			}
		
		
	}
	
//	public static void main(String args[])
//	{
//		fileName = args[1];
//		int port = Integer.parseInt(args[0]);
//		FileReader fr;
//		BufferedReader br;
//		
//		try
//		{
//			datFile = new File(fileName);
//			lastModified = new Date(datFile.lastModified());
//			fr = new FileReader(datFile);
//			br = new BufferedReader(fr);
//			noOfNeighbors = Integer.parseInt(br.readLine());
//			distanceVector = new Vector[noOfNeighbors];
//			dSocket = new DatagramSocket(port);
//			int i = 1;
//			String line, node;
//			Double cost;
//			while(i <= noOfNeighbors)
//			{
//				line = br.readLine();
//				node = line.split(" ")[0];
//				cost = Double.parseDouble(line.split(" ")[1]);
//				neighboringNodes[i] = node;
//				distanceVector[i].setNode(node);
//				distanceVector[i].setCost(cost);
//				neighboringPorts[i] = Integer.parseInt(args[2].split(":")[i]);
//			}
//		}
//		catch(Exception e)
//		{
//			
//		}
//		
//	}
	
	
	
	void calculateLinkCosts()
	{
		datFile = new File(fileName);
		FileReader fr;
		BufferedReader br;
		try{
			fr = new FileReader(datFile);
			br = new BufferedReader(fr);
			int i = 1;
			String line, node;
			Double cost;
			while(i <= noOfNeighbors)
			{
				line = br.readLine();
				node = line.split(" ")[0];
				cost = Double.parseDouble(line.split(" ")[1]);
				distanceVector.get(i).setNode(node);
				distanceVector.get(i).setCost(cost);
			}
		}
		catch(Exception e) {
			
		}
	}
	
	public void updateDistanceVector(ArrayList<Vector> vector, int port) {
		int i = 0,j;
		while(i < neighboringNodes.length) {
			if(neighboringPorts[i] == port)
				break;
			i++;
		}
		
		double costToThisNode = distanceVector.get(i).getCost();

		for(i = 0; i < vector.size(); i++) {
			String dest = vector.get(i).getNode();
			double tempCost = costToThisNode+vector.get(i).getCost();
			for(j = 0; j < distanceVector.size(); j++) {
				if(distanceVector.get(j).getNode().equals(dest)) {
					if(tempCost < distanceVector.get(j).getCost()) {
						distanceVector.get(j).setCost(tempCost);
					}
					break;
				}
			}
			if(j == distanceVector.size()) {
				Vector newDest = new Vector();
				newDest.setNode(dest);
				newDest.setCost(tempCost);
				distanceVector.add(newDest);
				
			}
			
		}
		
		for(i = 0; i < distanceVector.size(); i++) {
			System.out.println(Router.myName+"'s updated distance Vector:");
			System.out.println("Cost to "+distanceVector.get(i).getNode()+" is : "+distanceVector.get(i).getCost());
		}
		
		
	}
	
	public void receiveVector() {
		while(true) {
			try {
				byte[] incomingData = new byte[2048];
				int size = incomingData.length;
				DatagramPacket packet = new DatagramPacket(incomingData,size);
				dSocket.receive(packet);
				byte[] data = packet.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				ArrayList<Vector> vector = (ArrayList<Vector>) is.readObject();
				updateDistanceVector(vector,packet.getPort());
			}
			catch(Exception e) {
			
			}
		}
		
	}
	
	public void sendVector() {
		while(true) {
			try {
				if(lastModified.equals(new Date(datFile.lastModified()))) {
					calculateLinkCosts();
				}
				Thread.sleep(15000);
				IPAddress = InetAddress.getByName("localhost");
				for(int i = 0; i < neighboringNodes.length; i++) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(outputStream);
					os.writeObject(distanceVector);
					byte[] data = outputStream.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, neighboringPorts[i]);
					dSocket.send(sendPacket);
				
				}
			}
			catch(Exception e) {
			
			}
		}
	}

}
