package s.android.ffmpeg.model;

import java.sql.Timestamp;
import java.util.Arrays;

/**
 * @修改人：邓风森
 * @修改时间： 2015/2/2 14:50.
 */
public class LessonDto {
    private Integer id;
    private String courseName;
    private String remark;
    private Integer courseStatus;
    private Integer coachId;
    private String coachName;
    private Integer studentId;
    private String studentName;
    private Integer lastTime;
    private String lessonContent;
    private Timestamp lessonTime;
    private Integer classStatus;
    private String lessonStatus;
    private Timestamp realStartTime;
    private Timestamp realEndTime;
    private byte[] coachPhoto;

    public byte[] getCoachPhoto() {
        return coachPhoto;
    }

    public void setCoachPhoto(byte[] coachPhoto) {
        this.coachPhoto = coachPhoto;
    }

    public LessonDto() {
    }

    /**
     * @param id            课程id
     * @param courseName    课程名称
     * @param remark        课程备注
     * @param courseStatus  课程状态
     * @param coachId       讲师id
     * @param coachName     讲师名称
     * @param studentId     学生id
     * @param studentName   学生姓名
     * @param lastTime      上课时长
     * @param lessonContent 上课内容
     * @param lessonTime    上课时间
     * @param classStatus   教室状态
     * @param lessonStatus  课程状态
     * @param realStartTime 实际开始时间
     * @param realEndTime   实际结束时间
     */
    public LessonDto(Integer id, String courseName, String remark, Integer courseStatus, Integer coachId, String coachName, Integer studentId, String studentName, Integer lastTime, String lessonContent, Timestamp lessonTime, Integer classStatus, String lessonStatus, Timestamp realStartTime, Timestamp realEndTime, byte[] coachPhoto) {
        this.id = id;
        this.courseName = courseName;
        this.remark = remark;
        this.courseStatus = courseStatus;
        this.coachId = coachId;
        this.coachName = coachName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.lastTime = lastTime;
        this.lessonContent = lessonContent;
        this.lessonTime = lessonTime;
        this.classStatus = classStatus;
        this.lessonStatus = lessonStatus;
        this.realStartTime = realStartTime;
        this.realEndTime = realEndTime;
        this.coachPhoto = coachPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonDto lessonDto = (LessonDto) o;

        if (classStatus != null ? !classStatus.equals(lessonDto.classStatus) : lessonDto.classStatus != null)
            return false;
        if (coachId != null ? !coachId.equals(lessonDto.coachId) : lessonDto.coachId != null)
            return false;
        if (coachName != null ? !coachName.equals(lessonDto.coachName) : lessonDto.coachName != null)
            return false;
        if (!Arrays.equals(coachPhoto, lessonDto.coachPhoto)) return false;
        if (courseName != null ? !courseName.equals(lessonDto.courseName) : lessonDto.courseName != null)
            return false;
        if (courseStatus != null ? !courseStatus.equals(lessonDto.courseStatus) : lessonDto.courseStatus != null)
            return false;
        if (id != null ? !id.equals(lessonDto.id) : lessonDto.id != null) return false;
        if (lastTime != null ? !lastTime.equals(lessonDto.lastTime) : lessonDto.lastTime != null)
            return false;
        if (lessonContent != null ? !lessonContent.equals(lessonDto.lessonContent) : lessonDto.lessonContent != null)
            return false;
        if (lessonStatus != null ? !lessonStatus.equals(lessonDto.lessonStatus) : lessonDto.lessonStatus != null)
            return false;
        if (lessonTime != null ? !lessonTime.equals(lessonDto.lessonTime) : lessonDto.lessonTime != null)
            return false;
        if (realEndTime != null ? !realEndTime.equals(lessonDto.realEndTime) : lessonDto.realEndTime != null)
            return false;
        if (realStartTime != null ? !realStartTime.equals(lessonDto.realStartTime) : lessonDto.realStartTime != null)
            return false;
        if (remark != null ? !remark.equals(lessonDto.remark) : lessonDto.remark != null)
            return false;
        if (studentId != null ? !studentId.equals(lessonDto.studentId) : lessonDto.studentId != null)
            return false;
        if (studentName != null ? !studentName.equals(lessonDto.studentName) : lessonDto.studentName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (courseName != null ? courseName.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (courseStatus != null ? courseStatus.hashCode() : 0);
        result = 31 * result + (coachId != null ? coachId.hashCode() : 0);
        result = 31 * result + (coachName != null ? coachName.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        result = 31 * result + (studentName != null ? studentName.hashCode() : 0);
        result = 31 * result + (lastTime != null ? lastTime.hashCode() : 0);
        result = 31 * result + (lessonContent != null ? lessonContent.hashCode() : 0);
        result = 31 * result + (lessonTime != null ? lessonTime.hashCode() : 0);
        result = 31 * result + (classStatus != null ? classStatus.hashCode() : 0);
        result = 31 * result + (lessonStatus != null ? lessonStatus.hashCode() : 0);
        result = 31 * result + (realStartTime != null ? realStartTime.hashCode() : 0);
        result = 31 * result + (realEndTime != null ? realEndTime.hashCode() : 0);
        result = 31 * result + (coachPhoto != null ? Arrays.hashCode(coachPhoto) : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(Integer courseStatus) {
        this.courseStatus = courseStatus;
    }

    public Integer getCoachId() {
        return coachId;
    }

    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public String getLessonContent() {
        return lessonContent;
    }

    public void setLessonContent(String lessonContent) {
        this.lessonContent = lessonContent;
    }

    public Timestamp getLessonTime() {
        return lessonTime;
    }

    public void setLessonTime(Timestamp lessonTime) {
        this.lessonTime = lessonTime;
    }

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
    }

    public String getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(String lessonStatus) {
        this.lessonStatus = lessonStatus;
    }

    public Timestamp getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(Timestamp realStartTime) {
        this.realStartTime = realStartTime;
    }

    public Timestamp getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(Timestamp realEndTime) {
        this.realEndTime = realEndTime;
    }
}
