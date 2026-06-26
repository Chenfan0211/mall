package com.mall.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.api.station.dto.LeaderDTO;
import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaderDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.api.station.vo.LeaderQueryVO;
import com.mall.api.station.vo.StationLeaveApplyVO;
import com.mall.api.station.vo.StationLeaveQueryVO;
import com.mall.api.station.vo.StationQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.UsrLeader;
import com.mall.station.entity.UsrStation;
import com.mall.station.entity.UsrStationLeader;
import com.mall.station.entity.UsrStationLeave;
import com.mall.station.mapper.UsrLeaderMapper;
import com.mall.station.mapper.UsrStationLeaderMapper;
import com.mall.station.mapper.UsrStationLeaveMapper;
import com.mall.station.mapper.UsrStationMapper;
import com.mall.station.service.StationBaseService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class StationBaseServiceImpl extends StationReadSupport implements StationBaseService {

    private static final Long LEAVE_WAIT_AUDIT = 10L;
    private static final Long LEAVE_PASS = 20L;
    private static final Long LEAVE_CANCELLED = 40L;
    private static final DateTimeFormatter NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final UsrStationMapper stationMapper;
    private final UsrLeaderMapper leaderMapper;
    private final UsrStationLeaderMapper stationLeaderMapper;
    private final UsrStationLeaveMapper stationLeaveMapper;
    private final StationConvert stationConvert;

    public StationBaseServiceImpl(
            final UsrStationMapper stationMapper,
            final UsrLeaderMapper leaderMapper,
            final UsrStationLeaderMapper stationLeaderMapper,
            final UsrStationLeaveMapper stationLeaveMapper,
            final StationConvert stationConvert) {
        this.stationMapper = stationMapper;
        this.leaderMapper = leaderMapper;
        this.stationLeaderMapper = stationLeaderMapper;
        this.stationLeaveMapper = stationLeaveMapper;
        this.stationConvert = stationConvert;
    }

    @Override
    public PageResult<StationDTO> pageStations(final StationQueryVO query) {
        final StationQueryVO safeQuery = query == null ? new StationQueryVO() : query;
        final LambdaQueryWrapper<UsrStation> wrapper = new LambdaQueryWrapper<UsrStation>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(UsrStation::getStationNo, safeQuery.getKeyword())
                        .or()
                        .like(UsrStation::getStationName, safeQuery.getKeyword())
                        .or()
                        .like(UsrStation::getContactName, safeQuery.getKeyword())
                        .or()
                        .like(UsrStation::getContactMobile, safeQuery.getKeyword()))
                .eq(safeQuery.getCityId() != null, UsrStation::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, UsrStation::getId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, UsrStation::getStatus, safeQuery.getStatus())
                .orderByDesc(UsrStation::getId);
        return toPage(safeQuery, stationMapper, wrapper, stationConvert::toStationDTO);
    }

    @Override
    public StationDTO getStation(final Long id) {
        return getById(id, stationMapper, stationConvert::toStationDTO, "自提点");
    }

    @Override
    public PageResult<LeaderDTO> pageLeaders(final LeaderQueryVO query) {
        final LeaderQueryVO safeQuery = query == null ? new LeaderQueryVO() : query;
        final java.util.List<Long> leaderIds = listLeaderIdsByStation(safeQuery.getStationId());
        final LambdaQueryWrapper<UsrLeader> wrapper = new LambdaQueryWrapper<UsrLeader>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(UsrLeader::getLeaderNo, safeQuery.getKeyword())
                        .or()
                        .like(UsrLeader::getLeaderName, safeQuery.getKeyword())
                        .or()
                        .like(UsrLeader::getLeaderMobile, safeQuery.getKeyword()))
                .eq(safeQuery.getLeaderId() != null, UsrLeader::getId, safeQuery.getLeaderId())
                .eq(safeQuery.getStatus() != null, UsrLeader::getStatus, safeQuery.getStatus())
                .in(!leaderIds.isEmpty(), UsrLeader::getId, leaderIds)
                .eq(safeQuery.getStationId() != null && leaderIds.isEmpty(), UsrLeader::getId, -1L)
                .orderByDesc(UsrLeader::getId);
        return toPage(safeQuery, leaderMapper, wrapper, stationConvert::toLeaderDTO);
    }

    @Override
    public LeaderDTO getLeader(final Long id) {
        return getById(id, leaderMapper, stationConvert::toLeaderDTO, "团长");
    }

    @Override
    public PageResult<StationLeaderDTO> pageStationLeaders(final LeaderQueryVO query) {
        final LeaderQueryVO safeQuery = query == null ? new LeaderQueryVO() : query;
        final LambdaQueryWrapper<UsrStationLeader> wrapper = new LambdaQueryWrapper<UsrStationLeader>()
                .eq(safeQuery.getStationId() != null, UsrStationLeader::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getLeaderId() != null, UsrStationLeader::getLeaderId, safeQuery.getLeaderId())
                .eq(safeQuery.getBindStatus() != null, UsrStationLeader::getBindStatus, safeQuery.getBindStatus())
                .orderByDesc(UsrStationLeader::getId);
        return toPage(safeQuery, stationLeaderMapper, wrapper, stationConvert::toStationLeaderDTO);
    }

    @Override
    public StationLeaderDTO getStationLeader(final Long id) {
        return getById(id, stationLeaderMapper, stationConvert::toStationLeaderDTO, "自提点团长绑定");
    }

    @Override
    public PageResult<StationLeaveDTO> pageLeaves(final StationLeaveQueryVO query) {
        final StationLeaveQueryVO safeQuery = query == null ? new StationLeaveQueryVO() : query;
        final LambdaQueryWrapper<UsrStationLeave> wrapper = new LambdaQueryWrapper<UsrStationLeave>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(UsrStationLeave::getLeaveNo, safeQuery.getKeyword())
                        .or()
                        .like(UsrStationLeave::getReason, safeQuery.getKeyword()))
                .eq(safeQuery.getCityId() != null, UsrStationLeave::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, UsrStationLeave::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getLeaderId() != null, UsrStationLeave::getLeaderId, safeQuery.getLeaderId())
                .eq(safeQuery.getAuditStatus() != null, UsrStationLeave::getAuditStatus,
                        safeQuery.getAuditStatus())
                .orderByDesc(UsrStationLeave::getId);
        return toPage(safeQuery, stationLeaveMapper, wrapper, stationConvert::toStationLeaveDTO);
    }

    @Override
    public StationLeaveDTO getLeave(final Long id) {
        return getById(id, stationLeaveMapper, stationConvert::toStationLeaveDTO, "休假申请");
    }

    @Override
    @Transactional
    public StationLeaveDTO applyLeave(final StationLeaveApplyVO request) {
        if (request == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "休假申请不能为空");
        }
        validateLeaveRequest(request);
        final UsrStation station = stationMapper.selectById(request.getStationId());
        if (station == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "自提点不存在");
        }
        if (request.getLeaderId() != null) {
            final UsrLeader leader = leaderMapper.selectById(request.getLeaderId());
            if (leader == null) {
                throw new BusinessException(CommonErrorCode.NOT_FOUND, "团长不存在");
            }
        }
        validateNoLeaveOverlap(request);
        final LocalDateTime now = LocalDateTime.now();
        final UsrStationLeave leave = new UsrStationLeave();
        prepareCreate(leave, IdWorker.getId(), now);
        leave.setLeaveNo(buildNo("LV", now));
        leave.setStationId(request.getStationId());
        leave.setLeaderId(request.getLeaderId());
        leave.setStartTime(request.getStartTime());
        leave.setEndTime(request.getEndTime());
        leave.setReason(request.getReason());
        leave.setAuditStatus(LEAVE_WAIT_AUDIT);
        leave.setAuditAccountId(null);
        leave.setAuditTime(null);
        leave.setAuditRemark(null);
        leave.setCityId(station.getCityId());
        stationLeaveMapper.insert(leave);
        return stationConvert.toStationLeaveDTO(leave);
    }

    @Override
    @Transactional
    public StationLeaveDTO cancelLeave(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "休假申请ID不能为空");
        }
        final UsrStationLeave leave = stationLeaveMapper.selectById(id);
        if (leave == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "休假申请不存在");
        }
        if (Objects.equals(leave.getAuditStatus(), LEAVE_CANCELLED)) {
            return stationConvert.toStationLeaveDTO(leave);
        }
        if (!Objects.equals(leave.getAuditStatus(), LEAVE_WAIT_AUDIT)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有待审核休假申请才能撤回");
        }
        final LocalDateTime now = LocalDateTime.now();
        leave.setAuditStatus(LEAVE_CANCELLED);
        leave.setAuditTime(now);
        leave.setAuditRemark("申请人撤回");
        prepareUpdate(leave, now);
        stationLeaveMapper.updateById(leave);
        return stationConvert.toStationLeaveDTO(leave);
    }

    private java.util.List<Long> listLeaderIdsByStation(final Long stationId) {
        if (stationId == null) {
            return java.util.List.of();
        }
        return stationLeaderMapper.selectList(new LambdaQueryWrapper<UsrStationLeader>()
                        .eq(UsrStationLeader::getStationId, stationId))
                .stream()
                .map(UsrStationLeader::getLeaderId)
                .toList();
    }

    private void validateLeaveRequest(final StationLeaveApplyVO request) {
        if (request.getStationId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "自提点ID不能为空");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "休假开始和结束时间不能为空");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "休假结束时间必须晚于开始时间");
        }
        if (!StringUtils.hasText(request.getReason())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "休假原因不能为空");
        }
    }

    private void validateNoLeaveOverlap(final StationLeaveApplyVO request) {
        final LambdaQueryWrapper<UsrStationLeave> wrapper = new LambdaQueryWrapper<UsrStationLeave>()
                .eq(UsrStationLeave::getStationId, request.getStationId())
                .eq(request.getLeaderId() != null, UsrStationLeave::getLeaderId, request.getLeaderId())
                .isNull(request.getLeaderId() == null, UsrStationLeave::getLeaderId)
                .in(UsrStationLeave::getAuditStatus, List.of(LEAVE_WAIT_AUDIT, LEAVE_PASS))
                .lt(UsrStationLeave::getStartTime, request.getEndTime())
                .gt(UsrStationLeave::getEndTime, request.getStartTime());
        if (stationLeaveMapper.selectCount(wrapper) > 0L) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "当前时间段已有休假申请");
        }
    }

    private String buildNo(final String prefix, final LocalDateTime now) {
        return prefix + NO_TIME_FORMATTER.format(now) + IdWorker.getIdStr();
    }
}
