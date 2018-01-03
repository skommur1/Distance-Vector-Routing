package com;

import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramSocket;

public class Main {
	
	public static void main(String args[])
	{
		Router receiver = new Router("r");
		Router.fileName = args[1];
		Router.myName = args[3];
		int port = Integer.parseInt(args[0]);
		FileReader fr;
		BufferedReader br;
		
		try
		{
			Router.datFile = new File(Router.fileName);
			Router.lastModified = new Date(Router.datFile.lastModified());
			fr = new FileReader(Router.datFile);
			br = new BufferedReader(fr);
			Router.noOfNeighbors = Integer.parseInt(br.readLine());
			Router.distanceVector = new ArrayList<Vector>();
			Router.dSocket = new DatagramSocket(port);
			int i = 0;
			String line, node;
			Double cost;
			Vector meToMe = new Vector();
			meToMe.setCost(0.0);
			meToMe.setNode(Router.myName);
			Router.neighboringNodes = new String[Router.noOfNeighbors];
			Router.neighboringPorts = new int[Router.noOfNeighbors];
			System.out.println(Router.noOfNeighbors);
			while(i < Router.noOfNeighbors)
			{
				line = br.readLine();
				System.out.println(line);
				node = line.split(" ")[0];
				cost = Double.parseDouble(line.split(" ")[1]);
				Vector v = new Vector();
				v.setNode(node);
				v.setCost(cost);
				Router.neighboringNodes[i] = node;
				Router.distanceVector.add(v);
				Router.neighboringPorts[i] = Integer.parseInt(args[2].split(":")[i]);
				i++;
			}
			
			receiver.start();
			Router sender = new Router("s");
			sender.start();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		    
	}

}
