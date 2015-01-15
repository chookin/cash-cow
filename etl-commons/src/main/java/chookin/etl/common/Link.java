package chookin.etl.common;

/**
 * web page link
 */
public class Link implements Comparable<Link> {
	private String href;

	private String text;

	private String title;
	private float weight = 1f;
	public Link setWeight(float weight){
		this.weight = weight;
		return this;
	}
	public float getWeight(){
		return this.weight;
	}
	public String getHref() {
		return this.href;
	}

	public Link setHref(String href) {
		this.href = LinkHelper.trimUrl(href);
		return this;
	}

	public String getText() {
		return this.text;
	}

	public Link setText(String text) {
		this.text = text;
		return this;
	}

	public String getTitle() {
		return this.title;
	}

	public Link setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean equals(String href) {
		String myHref = LinkHelper.trimUrl(href);
		return this.href.compareTo(myHref) == 0;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Link)) {
			return false;
		}
		Link rhs = (Link) (obj);
		return this.equals(rhs.href);
	}

	@Override
	public int hashCode() {
		return this.href.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.href);
		if (this.text == null || this.text.isEmpty()
				|| this.text.compareToIgnoreCase(this.href) == 0) {
		} else {
			strb.append(" text: ").append(this.text);
		}
		if (this.title == null || this.title.isEmpty()) {
		} else {
			strb.append(" title: ").append(this.title);
		}
		return strb.toString();
	}

	@Override
	public int compareTo(Link arg0) {
		return this.href.compareTo(arg0.href);
	}
}
