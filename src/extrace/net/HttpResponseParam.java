package extrace.net;


//HTTPӦ�𷵻ز���

public class HttpResponseParam{
	public RETURN_STATUS statusCode;	//״̬��
	public String responseClassName;	//���ж��ַ��صĶ������ʱ,���������������
	public String responseString;		//��Ӧ��ʵ��JSON�ַ���
	//String responseMessage;		//��Ӧ����Ϣ�ַ���
	public HttpResponseParam(){
		statusCode = RETURN_STATUS.Ok;
		responseClassName = "";
	}

	public enum RETURN_STATUS{
		Ok,
		Saved,
		RequestException,
		ResponseException,
		ServerException,
		ObjectNotFoundException,
		NetworkException,
		Unknown
	}
}
