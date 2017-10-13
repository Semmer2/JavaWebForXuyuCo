package com.jl.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;

import java.io.*;
import java.net.*;
import java.util.*;

import org.xml.sax.InputSource;

public class WsdlUtil {
	public static final String FAIL_EXIST = "failExist";
	public static final String FAIL_PROTOCOL = "failProtocol";
	public static final String NULL = "null";
	private String error;
	public List service;
	public List operation;
	public String targeturl;

	public WsdlUtil() {

		service = new ArrayList();
		operation = new ArrayList();

	}

	public String getError() {
		return error;
	}

	public boolean parseFromContent(String wsdlcontent) throws Exception {
		boolean rtn = false;
		InputStream in = null;
		try {
			int beginpos = 0;
			int endpos = 0;
			String beginstr = "<![CDATA[";
			String endstr = "]]";
			beginpos = wsdlcontent.indexOf(beginstr) + beginstr.length();
			endpos = wsdlcontent.indexOf(endstr);
			if (endpos > beginpos)
				wsdlcontent = wsdlcontent.substring(beginpos, endpos);
			if (wsdlcontent.indexOf("<?xml version=") == -1)
				wsdlcontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + wsdlcontent;
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
			wsdlReader.setFeature("javax.wsdl.importDocuments", false);
			byte[] bytes = wsdlcontent.getBytes();
			in = new ByteArrayInputStream(bytes);
			Definition def = wsdlReader.readWSDL(null, new InputSource(in));
			/** Service **/
			Map<String, Service> services = def.getServices();
			Map<String, Port> ports;
			Service tempService;
			Port tempPort;
			HTTPAddress tempHTTPAddr;
			SOAPAddress tempSOAPAddr;
			String tempProtocolImpl;
			String serviceName, serviceURI = null, serviceDoc = null;
			int serviceId = 0;
			for (Map.Entry<String, Service> entryService : services.entrySet()) {
				tempService = (Service) entryService.getValue();
				ports = tempService.getPorts();
				serviceName = tempService.getQName().getLocalPart();// Service
				service.add(serviceName);//name
				Iterator portIter = ports.values().iterator();
				while (portIter.hasNext()) {
					Port port = (Port) portIter.next();
					Vector addrElems = findExtensibilityElement(port
							.getExtensibilityElements(), "address");
					ExtensibilityElement element = (ExtensibilityElement) addrElems
							.elementAt(0);
					if (element != null && element instanceof SOAPAddress) {
						SOAPAddress soapAddr = (SOAPAddress) element;
						this.targeturl = soapAddr.getLocationURI();
					}
				}
			}
			/** PortType **/
			Map<String, PortType> portTypes = def.getPortTypes();
			PortType tempPortType;
			List<Operation> operations;
			org.w3c.dom.Element tempDocElem;
			Input tempIn;
			Output tempOut;
			Message tempMsg;
			Map<String, Part> tempParts;
			Part tempPart;

			String portName, operationName, operationDoc = null, messageName, partName, partType = null;
			int portId, operationId, messageId;
			for (Entry<String, PortType> entryPortType : portTypes.entrySet()) {
				tempPortType = (PortType) entryPortType.getValue();
				portName = tempPortType.getQName().getLocalPart();
				/** Operation **/
				operations = tempPortType.getOperations();
				for (int i = 0; i < operations.size(); i++) {
					operationName = operations.get(i).getName();// Operation
					operation.add(operationName);
					/** Message **/
					tempIn = operations.get(i).getInput();// Input message
					messageName = tempIn.getMessage().getQName().getLocalPart();// Input
					tempMsg = def.getMessage(tempIn.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
					tempOut = operations.get(i).getOutput();// Output message
					messageName = tempOut.getMessage().getQName()
							.getLocalPart();// Output message name

					tempMsg = def.getMessage(tempOut.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
				}
			}
			rtn = true;
		} catch (Exception e) {
			rtn = false;
			error = e.getMessage();
		} finally {
			in.close();
		}
		return rtn;
	}

	@SuppressWarnings("unchecked")
	public boolean parseFromFile(String filename) throws Exception {
		boolean rtn = false;
		InputStream in = null;
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);

			WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
			wsdlReader.setFeature("javax.wsdl.importDocuments", false);
			File file = new File(filename);
			in = new FileInputStream(file);
			Definition def = wsdlReader.readWSDL(null, new InputSource(in));
			/** Service **/
			Map<String, Service> services = def.getServices();
			Map<String, Port> ports;
			Service tempService;
			Port tempPort;
			HTTPAddress tempHTTPAddr;
			SOAPAddress tempSOAPAddr;
			String tempProtocolImpl;
			String serviceName, serviceURI = null, serviceDoc = null;
			int serviceId = 0;
			for (Map.Entry<String, Service> entryService : services.entrySet()) {
				tempService = (Service) entryService.getValue();
				ports = tempService.getPorts();
				serviceName = tempService.getQName().getLocalPart();// Service																	
				service.add(serviceName);// name
				Iterator portIter = ports.values().iterator();
				while (portIter.hasNext()) {
					Port port = (Port) portIter.next();
					Vector addrElems = findExtensibilityElement(port
							.getExtensibilityElements(), "address");
					ExtensibilityElement element = (ExtensibilityElement) addrElems
							.elementAt(0);
					if (element != null && element instanceof SOAPAddress) {
						SOAPAddress soapAddr = (SOAPAddress) element;
						this.targeturl = soapAddr.getLocationURI();
					}
				}
			}

			/** PortType **/
			Map<String, PortType> portTypes = def.getPortTypes();
			PortType tempPortType;
			List<Operation> operations;
			org.w3c.dom.Element tempDocElem;
			Input tempIn;
			Output tempOut;
			Message tempMsg;
			Map<String, Part> tempParts;
			Part tempPart;

			String portName, operationName, operationDoc = null, messageName, partName, partType = null;
			int portId, operationId, messageId;
			for (Entry<String, PortType> entryPortType : portTypes.entrySet()) {
				tempPortType = (PortType) entryPortType.getValue();
				portName = tempPortType.getQName().getLocalPart();

				/** Operation **/
				operations = tempPortType.getOperations();
				for (int i = 0; i < operations.size(); i++) {
					operationName = operations.get(i).getName();// Operation
					operation.add(operationName);
					/** Message **/
					tempIn = operations.get(i).getInput();// Input message
					messageName = tempIn.getMessage().getQName().getLocalPart();// Input
																				// message
																				// name

					tempMsg = def.getMessage(tempIn.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();

						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
					tempOut = operations.get(i).getOutput();// Output message
					messageName = tempOut.getMessage().getQName()
							.getLocalPart();// Output message name

					tempMsg = def.getMessage(tempOut.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
				}
			}
			rtn = true;
		} catch (Exception e) {
			rtn = false;
			error = e.getMessage();
		} finally {
			in.close();
		}
		return rtn;
	}

	public boolean parseFromUrl(String wsdlURI, String username, String pwd)
			throws Exception {
		boolean rtn = false;
		if (wsdlURI == null) {
			return false;
		}
		HttpURLConnection conn = null;
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			URL url = new URL(wsdlURI);
			// get wsdl WITHOUT basic authentication info
			conn = (HttpURLConnection) url.openConnection();
			String input = username + ":" + pwd;
			String encoding = base64Encode(input);
			conn.setRequestProperty("Authorization", "Basic " + encoding);
			// connection.connect();
			// int errorcode=conn.getResponseCode();
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(30 * 1000);
			conn.setUseCaches(false);
			WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
			wsdlReader.setFeature("javax.wsdl.importDocuments", false);
			Definition def = wsdlReader.readWSDL(null, new InputSource(conn
					.getInputStream()));
			/** Service **/
			Map<String, Service> services = def.getServices();
			Map<String, Port> ports;
			Service tempService;
			Port tempPort;
			HTTPAddress tempHTTPAddr;
			SOAPAddress tempSOAPAddr;
			String tempProtocolImpl;
			String serviceName, serviceURI = null, serviceDoc = null;
			int serviceId = 0;
			for (Map.Entry<String, Service> entryService : services.entrySet()) {
				tempService = (Service) entryService.getValue();
				ports = tempService.getPorts();
				serviceName = tempService.getQName().getLocalPart();// Service																	
				service.add(serviceName);//name
				Iterator portIter = ports.values().iterator();
				while (portIter.hasNext()) {
					Port port = (Port) portIter.next();
					Vector addrElems = findExtensibilityElement(port
							.getExtensibilityElements(), "address");
					ExtensibilityElement element = (ExtensibilityElement) addrElems
							.elementAt(0);
					if (element != null && element instanceof SOAPAddress) {
						SOAPAddress soapAddr = (SOAPAddress) element;
						this.targeturl = soapAddr.getLocationURI();
					}
				}
			}
			/** PortType **/
			Map<String, PortType> portTypes = def.getPortTypes();
			PortType tempPortType;
			List<Operation> operations;
			org.w3c.dom.Element tempDocElem;
			Input tempIn;
			Output tempOut;
			Message tempMsg;
			Map<String, Part> tempParts;
			Part tempPart;

			String portName, operationName, operationDoc = null, messageName, partName, partType = null;
			int portId, operationId, messageId;
			for (Entry<String, PortType> entryPortType : portTypes.entrySet()) {
				tempPortType = (PortType) entryPortType.getValue();
				portName = tempPortType.getQName().getLocalPart();
				/** Operation **/
				operations = tempPortType.getOperations();
				for (int i = 0; i < operations.size(); i++) {
					operationName = operations.get(i).getName();// Operation
					operation.add(operationName);
					/** Message **/
					tempIn = operations.get(i).getInput();// Input message
					messageName = tempIn.getMessage().getQName().getLocalPart();// Input
					tempMsg = def.getMessage(tempIn.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
					tempOut = operations.get(i).getOutput();// Output message
					messageName = tempOut.getMessage().getQName()
							.getLocalPart();// Output message name

					tempMsg = def.getMessage(tempOut.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
				}
			}
			rtn = true;
		} catch (Exception e) {
			rtn = false;
			error = e.getMessage();
		} finally {
			conn.disconnect();
		}
		return rtn;
	}

	public boolean parseFromUrl(String wsdlURI) throws Exception {
		boolean rtn = false;
		if (wsdlURI == null) {
			return false;
		}
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			Definition def = reader.readWSDL(wsdlURI);
			/** Service **/
			Map<String, Service> services = def.getServices();
			Map<String, Port> ports;
			Service tempService;
			Port tempPort;
			HTTPAddress tempHTTPAddr;
			SOAPAddress tempSOAPAddr;
			String tempProtocolImpl;
			String serviceName, serviceURI = null, serviceDoc = null;
			int serviceId = 0;

			for (Map.Entry<String, Service> entryService : services.entrySet()) {
				tempService = (Service) entryService.getValue();
				ports = tempService.getPorts();
				serviceName = tempService.getQName().getLocalPart();// Service
				service.add(serviceName);//name
				Iterator portIter = ports.values().iterator();
				while (portIter.hasNext()) {
					Port port = (Port) portIter.next();
					Vector addrElems = findExtensibilityElement(port
							.getExtensibilityElements(), "address");
					ExtensibilityElement element = (ExtensibilityElement) addrElems
							.elementAt(0);
					if (element != null && element instanceof SOAPAddress) {
						SOAPAddress soapAddr = (SOAPAddress) element;
						this.targeturl = soapAddr.getLocationURI();
					}
				}
			}

			/** PortType **/
			Map<String, PortType> portTypes = def.getPortTypes();
			PortType tempPortType;
			List<Operation> operations;
			org.w3c.dom.Element tempDocElem;
			Input tempIn;
			Output tempOut;
			Message tempMsg;
			Map<String, Part> tempParts;
			Part tempPart;

			String portName, operationName, operationDoc = null, messageName, partName, partType = null;
			int portId, operationId, messageId;
			for (Entry<String, PortType> entryPortType : portTypes.entrySet()) {
				tempPortType = (PortType) entryPortType.getValue();
				portName = tempPortType.getQName().getLocalPart();
				/** Operation **/
				operations = tempPortType.getOperations();
				for (int i = 0; i < operations.size(); i++) {
					operationName = operations.get(i).getName();// Operation
																// name
					operation.add(operationName);
					
					/** Message **/
					tempIn = operations.get(i).getInput();// Input message
					messageName = tempIn.getMessage().getQName().getLocalPart();// Input
					tempMsg = def.getMessage(tempIn.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
					tempOut = operations.get(i).getOutput();// Output message
					messageName = tempOut.getMessage().getQName()
							.getLocalPart();// Output message name

					tempMsg = def.getMessage(tempOut.getMessage().getQName());
					tempParts = tempMsg.getParts();
					for (Entry<String, Part> entryPart : tempParts.entrySet()) {
						tempPart = (Part) entryPart.getValue();
						partName = tempPart.getName();// Parameter name
						if (tempPart.getElementName() != null)// Complex type
						{
							partType = tempPart.getElementName().getLocalPart();
						} else if (tempPart.getTypeName() != null)// Simple type
						{
							partType = tempPart.getTypeName().getLocalPart();
						}
					}
				}
			}
			rtn = true;
		} catch (Exception e) {
			rtn = false;
			error = e.getMessage();
		}
		return rtn;
	}

	public static void main(String[] s) throws Exception {
		// 1 列出不需要普通用户加密的WSDL中包括的服务与方法
		/*
		 * WsdlUtil wsdlutil0=newWsdlUtil(
		 * "http://jxepcd18.jx.sgcc.com.cn:8000/sap/bc/srt/rfc/sap/zyyjc_fm010/220/zyyjc_fm010/zyyjc_fm010?wsdl"
		 * ); if(wsdlutil0.parseFromurl()) {
		 * System.out.println("service:"+wsdlutil0.service.get(0).toString());
		 * for(int i=0;i<wsdlutil0.operation.size();i++) {
		 * System.out.println("operation:"
		 * +wsdlutil0.operation.get(i).toString()); } } else {
		 * System.out.println(wsdlutil0.getError()); }
		 */
		// 2 列出需要普通用户加密的WSDL中包括的服务与方法
		/*
		 * WsdlUtil wsdlutil1=newWsdlUtil(
		 * "http://jxepcd18.jx.sgcc.com.cn:8000/sap/bc/srt/wsdl/bndg_51100E08D1174585E10000000AEA0C20/wsdl11/allinone/ws_policy/document?sap-client=210"
		 * ); if(wsdlutil1.parseFromurl("esbrfc","654321")) {
		 * System.out.println("service:"+wsdlutil1.service.get(0).toString());
		 * for(int i=0;i<wsdlutil1.operation.size();i++) {
		 * System.out.println("operation:"
		 * +wsdlutil1.operation.get(i).toString()); } } else {
		 * System.out.println(wsdlutil1.getError()); }
		 */
		// 3 列出本地文件wsdl中包括的服务与方法
		WsdlUtil wsdlutil2 = new WsdlUtil();
		if (wsdlutil2
				.parseFromFile("E:\\Workspaces\\osbmanage\\jxepctestjsq.xml")) {
			System.out
					.println("service:" + wsdlutil2.service.get(0).toString());
			for (int i = 0; i < wsdlutil2.operation.size(); i++) {
				System.out.println("operation:"
						+ wsdlutil2.operation.get(i).toString());
			}
			System.out.println(wsdlutil2.targeturl);
		} else {
			System.out.println(wsdlutil2.getError());
		}
	}

	private String base64Encode(String s) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		Base64OutputStream out = new Base64OutputStream(bOut);
		try {
			out.write(s.getBytes());
			out.flush();
		} catch (IOException e) {
		}
		return bOut.toString();
	}

	private Vector findExtensibilityElement(List extensibilityElements,
			String elementType) {
		int i = 0;
		Vector elements = new Vector();
		if (extensibilityElements != null) {
			Iterator iter = extensibilityElements.iterator();
			while (iter.hasNext()) {
				ExtensibilityElement elment = (ExtensibilityElement) iter
						.next();
				if (elment.getElementType().getLocalPart().equalsIgnoreCase(
						elementType)) {
					elements.add(elment);
				}
			}
		}
		return elements;
	}
}

class Base64OutputStream extends FilterOutputStream {
	/**
	 * Constructs the stream filter
	 * 
	 * @param out
	 *            the stream to filter
	 */
	public Base64OutputStream(OutputStream out) {
		super(out);
	}

	public void write(int c) throws IOException {
		inbuf[i] = c;
		i++;
		if (i == 3) {
			super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
			super.write(toBase64[((inbuf[0] & 0x03) << 4)
					| ((inbuf[1] & 0xF0) >> 4)]);
			super.write(toBase64[((inbuf[1] & 0x0F) << 2)
					| ((inbuf[2] & 0xC0) >> 6)]);
			super.write(toBase64[inbuf[2] & 0x3F]);
			col += 4;
			i = 0;
			if (col >= 76) {
				super.write('\n');
				col = 0;
			}
		}
	}

	public void flush() throws IOException {
		if (i == 1) {
			super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
			super.write(toBase64[(inbuf[0] & 0x03) << 4]);
			super.write('=');
			super.write('=');
		} else if (i == 2) {
			super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
			super.write(toBase64[((inbuf[0] & 0x03) << 4)
					| ((inbuf[1] & 0xF0) >> 4)]);
			super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
			super.write('=');
		}
	}

	private static char[] toBase64 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '+', '/' };

	private int col = 0;
	private int i = 0;
	private int[] inbuf = new int[3];
}
