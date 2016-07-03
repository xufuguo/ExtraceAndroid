package extrace.misc.model;

import java.util.Date;

public class Message {
	private int SN;
	private int sender;
	private int accepter;
	private String expId;
	private boolean isrecv;
	private Date time;
	private double x;
	private double y;
	/*
	 * ��������������Ϊ��ʹ�ͻ��˲����ٲ�ѯһ��name�������ݿ���û������ֶ�
	 */
	private String senderName;
	private String accepterName;
	private String tel;
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getAccepterName() {
		return accepterName;
	}
	public void setAccepterName(String accepterName) {
		this.accepterName = accepterName;
	}
	public int getSN() {
		return SN;
	}
	public void setSN(int sN) {
		SN = sN;
	}
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}
	public int getAccepter() {
		return accepter;
	}
	public void setAccepter(int accepter) {
		this.accepter = accepter;
	}
	public String getExpId() {
		return expId;
	}
	public void setExpId(String expId) {
		this.expId = expId;
	}
	public boolean isIsrecv() {
		return isrecv;
	}
	public void setIsrecv(boolean isrecv) {
		this.isrecv = isrecv;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "Message [SN=" + SN + ", sender=" + sender + ", accepter=" + accepter + ", expId=" + expId + ", isrecv="
				+ isrecv + ", time=" + time + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
