package com.herokuapp.polimiboardgamemanager.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;  

/**
 * A custom adapter for jax-rs links.
 * It solves marshaling and unmarshaling problems for links in json format.
 */
public class LinkAdapter  
extends XmlAdapter<LinkJaxb, Link> {  

	/**
	 * Instantiates a new link adapter.
	 */
	public LinkAdapter() {  
		super();
	}  

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Link unmarshal(LinkJaxb p1) {  

		Link.Builder builder = Link.fromUri(p1.getUri());  
		for (Map.Entry<QName, Object> entry : p1.getParams().entrySet()) {  
			builder.param(entry.getKey().getLocalPart(), entry.getValue().toString());  
		}  
		return builder.build();  
	}  

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public LinkJaxb marshal(Link p1) {  

		Map<QName, Object> params = new HashMap<>();  
		for (Map.Entry<String,String> entry : p1.getParams().entrySet()) {  
			params.put(new QName("", entry.getKey()), entry.getValue());  
		}  
		return new LinkJaxb(p1.getUri(), params);  
	}  
}  

class LinkJaxb  {  

	private URI uri;  
	private Map<QName, Object> params;  


	public LinkJaxb() {  
		this (null, null);  
	}  

	public LinkJaxb(URI uri) {  
		this(uri, null);  
	}  

	public LinkJaxb(URI uri, Map<QName, Object> map) {  

		this.uri = uri;  
		this.params = map!=null ? map : new HashMap<QName, Object>();  

	}  

	@XmlAttribute(name = "href")  
	public URI getUri() {   
		return uri;  
	}  

	@XmlAnyAttribute  
	public Map<QName, Object> getParams() {   
		return params;  
	}  

	public void setUri(URI uri) {  
		this.uri = uri;  
	}  

	public void setParams(Map<QName, Object> params) {  
		this.params = params;  
	}    
}
