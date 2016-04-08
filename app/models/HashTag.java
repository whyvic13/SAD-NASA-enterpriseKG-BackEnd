package models;

import javax.persistence.*;

@Entity
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "commentId", referencedColumnName = "commentId")
    private ClimateServiceComment climateServiceComment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "serviceId", referencedColumnName = "id")
    private ClimateService climateService;

    private String content;

    public HashTag() {
    }

    public HashTag(ClimateServiceComment climateServiceComment, ClimateService climateService, String content) {
        super();
        this.climateServiceComment = climateServiceComment;
        this.climateService = climateService;
        this.content = content;
    }

    public ClimateServiceComment getClimateServiceComment() {
        return climateServiceComment;
    }
    
    public void setClimateServiceComment(ClimateServiceComment climateServiceComment) {
        this.climateServiceComment = climateServiceComment;
    }

    public ClimateService getClimateService() {
        return climateService;
    }
    
    public void setClimateService(ClimateService climateService) {
        this.climateService = climateService;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "";
    }
}
