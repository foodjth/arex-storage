package com.arextest.storage.mock;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.mock.Mocker;
import java.util.Collections;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class MatchKeyFactory {

  private final List<MatchKeyBuilder> matchKeyBuilders;

  public MatchKeyFactory(List<MatchKeyBuilder> matchKeyBuilders) {
    this.matchKeyBuilders = matchKeyBuilders;
  }

  private MatchKeyBuilder find(MockCategoryType categoryType) {
    if (CollectionUtils.isNotEmpty(this.matchKeyBuilders)) {
      for (MatchKeyBuilder matchKeyBuilder : this.matchKeyBuilders) {
        if (matchKeyBuilder.isSupported(categoryType)) {
          return matchKeyBuilder;
        }
      }
    }
    return null;
  }

  public List<byte[]> build(@NotNull Mocker instance) {
    MatchKeyBuilder matchKeyBuilder = find(instance.getCategoryType());
    if (matchKeyBuilder == null) {
      LOGGER.warn("Could not found replay result match key builder for {}", instance);
      return Collections.emptyList();
    }
    return matchKeyBuilder.build(instance);
  }

  public String getEigenBody(@NotNull Mocker instance) {
    MatchKeyBuilder matchKeyBuilder = find(instance.getCategoryType());
    if (matchKeyBuilder == null) {
      LOGGER.warn("Could not get eigen body for {}", instance);
      return null;
    }

    if (instance.getTargetRequest() == null) {
      LOGGER.warn("failed to get eigen body, recordId: {}, category: {}", instance.getRecordId(), instance.getCategoryType());
      return null;
    }
    return matchKeyBuilder.getEigenBody(instance);
  }

}