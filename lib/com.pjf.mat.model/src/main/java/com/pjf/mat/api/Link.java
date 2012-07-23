package com.pjf.mat.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Link {
	
	public static final Set<Link> computeLinks(Collection<Element> elements) {
		Set<Link> links = new HashSet<Link>();
		for(Element element : elements) {
			for(InputPort to: element.getInputs()) {
				OutputPort src = to.getConnectedSrc();
				if (src != null) {
					Link lnk = new Link(element, to);
					links.add(lnk);
				}
			}
		}
		return links;
	}

	
	final Element owner;
	final InputPort local;
	OutputPort from;
	
	public Link(Element owner, InputPort local) {
		this.owner = owner;
		this.local = local;
		this.from = local.getConnectedSrc();
	}

	public Link(Element owner, InputPort local, OutputPort src) {
		this.owner = owner;
		this.local = local;
		this.from = src;
	}
	
	public void setFrom(OutputPort from) {
		this.from = from;
	}

	public InputPort target() {
		return this.local;
	}
	
	public OutputPort source() {
		return this.from;
	}
	
	public Element targetElement() {
		return owner;
	}
	
	
	public boolean isTo(Element element) {
		return owner.equals(element);
	}
	
	public boolean isFrom(Element element) {
		return from.getParent().equals(element);
	}

	@Override
	public int hashCode() {
		int code = 17;
		if (from != null) code += from.hashCode() * 37;
		if (local != null) code += local.hashCode() * 37;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Link)) return false;
		Link other = (Link)obj;
		return (from != null && from.equals(other.from)
				&& (local != null && local.equals(other.local)));
	}
	
	@Override	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(owner.getType()).append("/").append(local);
		sb.append(" <=== ");
		sb.append(from.getParent().getType()).append("/").append(from);
		sb.append("]");
		return sb.toString();
	}

}

