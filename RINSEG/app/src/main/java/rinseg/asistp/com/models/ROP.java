package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class ROP extends RealmObject{
    private int id;

    private int code;

    @SerializedName("risk_id")
    private int riskId;

    @SerializedName("event_id")
    private int eventId;

    @SerializedName("target_id")
    private int targetId;

    @SerializedName("area_id")
    private int areaId;

    @SerializedName("area_name")
    private String area;

    @SerializedName("event_place")
    private String eventPlace;

    @SerializedName("company_id")
    private int companyId;

    private CompanyRO company;

    private Date eventDate;

    @SerializedName("event_date")
    private String eventDateString;

    @SerializedName("event_description")
    private String eventDescription;

    @SerializedName("reporter_name")
    private String reporterName;

    @SerializedName("reporter_company")
    private String reporterCompany;

    @SerializedName("supervisor_name")
    private String supervisorName;

    @SerializedName("supervisor_company")
    private String supervisorCompany;

    private int supervisorIdCompany;
    private int userId;

    @SerializedName("research_required")
    private boolean researchRequired;

    private Date dateClose;

    @SerializedName("date_close")
    private String dateCloseString;

    @SerializedName("worker_commitment")
    private String workerCommitment;

    @SerializedName("commitment_accepted")
    private boolean commitmentAccept;

    @SerializedName("rop_item")
    public RealmList<AccionPreventiva> listaAccionPreventiva;

    public RealmList<ImagenRO> listaImgComent;


    public RealmList<EventItemsRO> listaEventItems;

    @SerializedName("event_item")
    public RealmList<EventItemsFix> listaEventItemsFix;

    public User usuarioCreador;

    private String tmpId;
    private boolean puedeElimianrse;
    private String imageFolder;

    private boolean cerrado;

    /** Estado del ROP : Pendiente = 0, Registrado = 1, Cerrado = 2 */
    @SerializedName("status")
    private int estadoRop;

    public ROP(){ }

    public ROP(int _id, Date _eventDate) {
        this.setId(_id);
        this.setEventDate(_eventDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int _code) {
        this.code = _code;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date _eventDate) {
        this.eventDate = _eventDate;
    }

    public String getTmpId() {
        return tmpId;
    }

    public void setTmpId(String _tmpId) {
        this.tmpId = _tmpId;
    }

    public int getRiskId() {return riskId;}

    public void setRiskId(int _riskId) {this.riskId = _riskId;}

    public int getEventId() {return eventId;}

    public void setEventId(int _eventId) {this.eventId = _eventId;}

    public int getTargetId() {return targetId;}

    public void setTargetId(int _targetId) {this.targetId = _targetId;}

    public int getAreaId() {return areaId;}

    public void setAreaId(int _areaId) {this.areaId = _areaId;}

    public String getArea() {return area;}

    public void setArea(String _area) {this.area = _area;}

    public String getEventPlace() {return eventPlace;}

    public void setEventPlace(String _eventPlace) {this.eventPlace = _eventPlace;}

    public int getCompanyId() {return companyId;}

    public void setCompanyId(int _companyId) {this.companyId = _companyId;}

    public CompanyRO getCompany() {return company;}

    public void setCompany(CompanyRO _company) {this.company = _company;}

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {this.eventDescription = eventDescription;}

    public String getReporterName() {return reporterName;}

    public void setReporterName(String _reporterName) {this.reporterName = _reporterName;}

    public String getReporterCompany() {return reporterCompany;}

    public void setReporterCompany(String _reporterCompany) {this.reporterCompany = _reporterCompany;}

    public boolean puedeElimianrse() { return puedeElimianrse;}

    public void setPuedeElimianrse(boolean _puedeElimianrse) {this.puedeElimianrse = _puedeElimianrse;}

    public String getImageFolder() {return this.imageFolder;}

    public void setImageFolder(String _imageFolder) {this.imageFolder = _imageFolder;}

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorCompany() {
        return supervisorCompany;
    }

    public void setSupervisorCompany(String supervisorCompany) {
        this.supervisorCompany = supervisorCompany;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public boolean isResearch_required() {
        return researchRequired;
    }

    public void setResearch_required(boolean research_required) {
        this.researchRequired = research_required;
    }

    public int getSupervisorIdCompany() {
        return supervisorIdCompany;
    }

    public void setSupervisorIdCompany(int supervisorIdCompany) {
        this.supervisorIdCompany = supervisorIdCompany;
    }

    public Date getDateClose() {
        return dateClose;
    }

    public void setDateClose(Date dateClose) {
        this.dateClose = dateClose;
    }

    public String getDateCloseString() {
        return dateCloseString;
    }

    public void setDateCloseString(String dateCloseString) {
        this.dateCloseString = dateCloseString;
    }

    public String getEventDateString() {
        return eventDateString;
    }

    public void setEventDateString(String eventDateString) {
        this.eventDateString = eventDateString;
    }

    public RealmList<EventItemsRO> getListaEventItems() {
        return listaEventItems;
    }

    public void setListaEventItems(RealmList<EventItemsRO> listaEventItems) {
        this.listaEventItems = listaEventItems;
    }

    public int getEstadoRop() {
        return estadoRop;
    }

    public void setEstadoRop(int estadoRop) {
        this.estadoRop = estadoRop;
    }

    public String getWorkerCommitment() {
        return workerCommitment;
    }

    public void setWorkerCommitment(String workerCommitment) {
        this.workerCommitment = workerCommitment;
    }

    public boolean isCommitmentAccept() {
        return commitmentAccept;
    }

    public void setCommitmentAccept(boolean commitmentAccept) {
        this.commitmentAccept = commitmentAccept;
    }

    public void setValues(JSONObject json) {
        try {
            setEstadoRop(1);
            setId(json.getInt("id"));
            setCode(json.getInt("code"));
            setRiskId(json.getInt("risk_id"));
            setEventId(json.getInt("event_id"));
            setTargetId(json.getInt("target_id"));
            setAreaId(json.getInt("area_id"));
            setArea(json.getString("area_name"));
            setEventPlace(json.getString("event_place"));
            setCompanyId(json.getInt("company_id"));
            setEventDateString(json.getString("event_date"));
            setEventDescription(json.getString("event_description"));
            setReporterName(json.getString("reporter_name"));
            setReporterCompany(json.getString("reporter_company"));
            setSupervisorName(json.getString("supervisor_name"));
            setSupervisorCompany(json.getString("supervisor_company"));
            setDateCloseString(json.getString("date_close"));
            setUserId(json.getInt("user_id"));
            setTmpId(String.valueOf(json.getInt("id")));
            setEstadoRop(json.getInt("status"));
            int research_required = json.getInt("research_required");
            if (research_required == 1) {
                setResearch_required(true);
            } else {
                setResearch_required(false);
            }
            Date eventDate = Generic.dateFormatterMySql.parse(getEventDateString());
            setEventDate(eventDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
