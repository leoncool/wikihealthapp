package uk.ac.imperial.wikihealth.DataUpload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * JSON formatting utils
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class JSONUtils {
	private String m_username;
	private String m_password;
	private String m_token="0432aefd0a5544b6baf8e10f2f5faa5e";
	public JSONUtils(String _username, String _password) {
		this.m_username=_username;
		this.m_password=_password;
		initConnection();
	}

	/**
	 * connection json build
	 * @return
	 */
	public String initConnection() {
		Log.i("initConnection","starting");
		try {
			JSONObject jsonObjSend = new JSONObject("{" + 
					"\"loginid\":\""+m_username+"\"," + 
					"\"password\":\""+m_password+"\"," + 
					"\"expire_in_seconds\":99999999" + 
					"}");
			Log.i("initConnection","sending request for token....");
			Log.i("init connetion",jsonObjSend.toString());
		
			JSONObject jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/users/gettoken?api_key=special-key",jsonObjSend);
			m_token = jsonObjReceive.getJSONObject("usertoken").getString("token");
			Log.i("initConnection","sucessful");

			return m_token;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.i("initConnection","error");

		return null;
	}
	
	/**
	 * Datastream request json object
	 * @return
	 */
	public String[] getDataStreamList(){
		Log.i("getDataStreamList","starting");

		JSONObject jsonObjReceive=null;
		
		jsonObjReceive=HttpClient.SendHttpGet("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health?accesstoken="+m_token+"&api_key=special-key");
		
		if(jsonObjReceive==null)
			System.out.println("null get");
		String [] datastreams;
		try {
			JSONArray array= jsonObjReceive.getJSONArray("datastream_list");
			datastreams=new String[array.length()];
			System.out.println("Length: "+array.length());
			for(int i=0;i<array.length();i++){
				datastreams[i]=array.getJSONObject(i).getString("title").toString();
			}
			Log.i("getDataStreamList","success");

			return datastreams;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("getDataStreamList","error");

		return null;
		
	}
	
	/**
	 * Creates the healthstream if it doesnt exist
	 * @param health_stream
	 * @param units
	 * @return
	 */
	public String createHealthDatastream(String health_stream,String [] units){
		Log.i("createHealthDatastream","start");

		
		try {
			JSONArray units_array=new JSONArray();
			
			for(int i=0;i<units.length;i++){
				JSONObject unit_object=new JSONObject();
				unit_object.put("unit_id",units[i]);
				unit_object.put("unit_label",units[i]);
				//unit_object.put("value_type",units[i]);
				units_array.put(unit_object);
			}
			
			JSONObject jsonObjSend = new JSONObject("{\n" +
					"  \"datastream_id\": \""+health_stream+"\",\n" + 
					"  \"title\": \""+health_stream+"\",\n" + 
					"  \"units_list\":"+units_array+"\n" + 
					"}");
			String result = units_array.toString();
			System.out.println("Unit Array: "+result);
			@SuppressWarnings("unused")
			JSONObject jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title?accesstoken="+m_token+"&api_key=special-key",jsonObjSend);
			//String result = jsonObjReceive.getString("result");
			Log.i("createHealthDatastream","success");

			return result;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("createHealthDatastream","error");

		
		return null;
	}

	private JSONObject getHealthStreamData(String datastream_name){
		

		System.out.println("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title/"+datastream_name+"?accesstoken="+m_token+"&api_key=special-key");
		JSONObject jsonObjReceive=HttpClient.SendHttpGet("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title/"+datastream_name+"?accesstoken="+m_token+"&api_key=special-key");
		return jsonObjReceive;
		
	}

	/**
	 * sends the data to the remote databse in order to be stored
	 * it spolits the data in small chunks so the memory won't get exhausted
	 * @param datastream_name
	 * @param columns
	 * @param table
	 * @return
	 */
	public String sendDatapointsToHealthStream(String datastream_name,String[] columns,ArrayList<HashMap<String,String>> table) {
		Log.i("sendDatapointsToHealthStream","start");
		HashMap<String, String> unit_ids=new HashMap<String,String>();
		JSONObject jsonObjReceive=null;
		
		do{
			jsonObjReceive=getHealthStreamData(datastream_name);
		}while(jsonObjReceive==null);
		
		try {
			//map column names to ids
			
			JSONArray unit_list=jsonObjReceive.getJSONObject("datastream").getJSONArray("units_list");
			for(int i =0;i<unit_list.length();i++){
				unit_ids.put(unit_list.getJSONObject(i).getString("unit_label").toString(), unit_list.getJSONObject(i).getString("unit_id").toString());
			}

			//handle all rows
			JSONArray all_rows = new JSONArray();
			for(HashMap<String,String> row : table){
				System.out.println("row"+row);
				System.out.println("columns"+columns);
					JSONArray units_array=new JSONArray();
					JSONObject data_point=new JSONObject();
					data_point.put("at", row.get("timestamp"));
					for(int i=0;i<columns.length;i++){
						JSONObject unit_object=new JSONObject();
						unit_object.put("unit_id",unit_ids.get(columns[i]));
						unit_object.put("val",row.get(columns[i]));
						System.out.println("Columns '"+columns[i]+"'"+row.get(columns[i])+"'");
						units_array.put(unit_object);
						System.out.println("array per sens"+units_array);
					}
					data_point.put("value_list", units_array);
					all_rows.put(data_point);
					
					if(all_rows.length()==15){
						
						
						JSONObject jsonObjSend = new JSONObject("{\n" + 
								"  \"data_points\":"+all_rows.toString() + 
								"}");
						
						System.out.println("sending these data points"+jsonObjSend.toString());
						jsonObjReceive=null;
						do{
						jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title/"+datastream_name+"/datapoints?accesstoken="+m_token+"&api_key=special-key",jsonObjSend);
						}while(jsonObjReceive==null);
						all_rows=null;
						all_rows = new JSONArray();
					}
					
					
			}
			if(all_rows.length()!=0){
			System.out.println("allrows of "+datastream_name+" :  "+ all_rows.length() + "\n"+"last row is : "+all_rows.getJSONObject(0).toString());
			
			if(all_rows.length()<15 && all_rows.length()!=0){
				
				
				JSONObject jsonObjSend = new JSONObject("{\n" + 
						"  \"data_points\":"+all_rows.toString() + 
						"}");
				
				System.out.println("sending these data points"+jsonObjSend.toString());
				jsonObjReceive=null;
				do{
				jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title/"+datastream_name+"/datapoints?accesstoken="+m_token+"&api_key=special-key",jsonObjSend);
				}while(jsonObjReceive==null);
				String result = jsonObjReceive.getString("result");
				Log.i("sendDatapointsToHealthStream","success");
	
				return result;
			}
			else{
				
				int first=0,last=15;
				
				while(first<all_rows.length()){
						
					String chunk="{\n" + 
							"  \"data_points\": [";
					for(int i=first;i<Math.min(last, all_rows.length());i++){
							chunk=chunk+all_rows.getJSONObject(i).toString()+","; 
					}
					chunk=chunk.substring(0, chunk.length()-1)+"]}";
					JSONObject jsonObjSend = new JSONObject(chunk);
					
					System.out.println(datastream_name+":  sending these data points"+jsonObjSend.toString());
					jsonObjReceive=null;
					do{
					jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/health/title/"+datastream_name+"/datapoints?accesstoken="+m_token+"&api_key=special-key",jsonObjSend);
					}while(jsonObjReceive==null);
			//		String result = jsonObjReceive.getString("result");
					first+=15;
					last+=15;
				}
		
					Log.i("sendDatapointsToHealthStream","success");
		
				return "ok";
			}
			}
			return "error";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("sendDatapointsToHealthStream","error");
		return null;
		
	}
	
	/**
	 * splits the data in small chunks
	 * @param s
	 * @param chunkSize
	 * @return
	 */
	public static List<String> splitInChunks(String s, int chunkSize) {
	    List<String> result = new ArrayList<String>();
	    int length = s.length();
	    for (int i = 0; i < length; i += chunkSize) {
	        result.add(s.substring(i, Math.min(length, i + chunkSize)));
	    }
	    return result;
	}

}
