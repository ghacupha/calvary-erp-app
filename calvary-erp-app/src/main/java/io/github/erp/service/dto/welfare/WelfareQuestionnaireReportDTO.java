package io.github.erp.service.dto.welfare;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * DTO summarising questionnaire submissions for staff reporting.
 */
public class WelfareQuestionnaireReportDTO implements Serializable {

    private long totalRegistrations;
    private long totalDependents;
    private Map<String, Long> membershipTypeBreakdown;

    public WelfareQuestionnaireReportDTO() {}

    public WelfareQuestionnaireReportDTO(long totalRegistrations, long totalDependents, Map<String, Long> membershipTypeBreakdown) {
        this.totalRegistrations = totalRegistrations;
        this.totalDependents = totalDependents;
        this.membershipTypeBreakdown = membershipTypeBreakdown;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public long getTotalDependents() {
        return totalDependents;
    }

    public void setTotalDependents(long totalDependents) {
        this.totalDependents = totalDependents;
    }

    public Map<String, Long> getMembershipTypeBreakdown() {
        return membershipTypeBreakdown;
    }

    public void setMembershipTypeBreakdown(Map<String, Long> membershipTypeBreakdown) {
        this.membershipTypeBreakdown = membershipTypeBreakdown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareQuestionnaireReportDTO)) {
            return false;
        }
        WelfareQuestionnaireReportDTO that = (WelfareQuestionnaireReportDTO) o;
        return totalRegistrations == that.totalRegistrations && totalDependents == that.totalDependents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRegistrations, totalDependents);
    }

    @Override
    public String toString() {
        return "WelfareQuestionnaireReportDTO{" +
            "totalRegistrations=" + totalRegistrations +
            ", totalDependents=" + totalDependents +
            ", membershipTypeBreakdown=" + membershipTypeBreakdown +
            '}';
    }
}
