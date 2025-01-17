package com.hqy.cloud.web.upload;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.util.CommonDateUtil;
import com.hqy.cloud.util.file.FileUtil;
import com.hqy.cloud.util.file.FileValidateContext;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.support.UploadContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AbstractUploadFileService.
 * @see UploadFileService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 11:13
 */
@Slf4j
@RefreshScope
public abstract class AbstractUploadFileService implements UploadFileService {
    private final UploadFileProperties properties;
    protected ExecutorService threadPool;

    public AbstractUploadFileService(UploadFileProperties properties) {
        this.properties = properties;
        this.threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, properties.getMaxThreadCore(),
                5 * 1000 * 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("Upload"));
    }

    @Override
    public UploadResponse uploadAvatar(MultipartFile file) throws UploadFileException {
        return uploadImgFile(AVATAR_FOLDER, file);
    }

    @Override
    public UploadResponse uploadImgFile(String folderPath, MultipartFile file) throws UploadFileException {
        return doUpload(folderPath, file, true);
    }

    @Override
    public UploadResponse uploadFile(String folderPath, MultipartFile file) throws UploadFileException {
        return doUpload(folderPath, file, false);
    }

    private UploadResponse doUpload(String folderPath, MultipartFile file, boolean isImageUpload) {
        UploadContext.UploadState uploadState = getUploadState();
        UploadMode.Mode uploadMode = uploadState.getMode();
        UploadResult result;
        if (StringUtils.isBlank(folderPath)) {
            result = UploadResult.failed("Upload image file folderPath should not be null.");
            return buildResponse(result, uploadMode);
        }
        if (!folderPath.startsWith(StringConstants.Symbol.INCLINED_ROD)) {
            folderPath = StringConstants.Symbol.INCLINED_ROD + folderPath;
        }
        String originalFilename = file.getOriginalFilename();
        //checking file type.
        boolean validate = validateFileType(originalFilename, isImageUpload);
        if (!validate) {
            result = UploadResult.failed("No support file type.");
            return buildResponse(result, uploadMode);
        }
        // check file.
        result = validateFile(file);
        if (!result.isResult()) {
            return buildResponse(result, uploadMode);
        }
        return writeFile(originalFilename, folderPath, uploadState, file);
    }


    @Override
    public void addUploadSupportImgFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_IMAGE_FILE_TYPES.add(fileType);
        log.info("Service add new support image file type, invalid after Service restart, image fileType: {}.", fileType);
    }

    @Override
    public void addUploadSupportFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_COMMON_FILE_TYPES.add(fileType);
        log.info("Service add new support file type, invalid after Service restart, fileType: {}.", fileType);
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 写文件.
     * @param originalFilename     起始文件名
     * @param folderPath           文件目录
     * @param state                文件上传方式，同步或异步
     * @param file                 文件
     * @return                     {@link UploadResponse}
     * @throws UploadFileException e.
     */
    protected abstract UploadResponse writeFile(String originalFilename, String folderPath, UploadContext.UploadState state, MultipartFile file) throws UploadFileException;


    private UploadContext.UploadState getUploadState() {
        return UploadContext.getState();
    }

    protected boolean validateFileType(String filename, boolean imageType) {
        if (imageType) {
            return FileUtil.validateImgFileType(filename);
        } else {
            return FileUtil.validateFileType(filename);
        }
    }

    protected String generateRelativeFilePath(String folder, String baseFileName) {
        return folder +
                StringConstants.Symbol.INCLINED_ROD +
                CommonDateUtil.today() +
                StringConstants.Symbol.INCLINED_ROD +
                baseFileName;
    }

    protected UploadResult validateFile(MultipartFile file) {
        long maxSize = properties.getSize().toMillis();
        if (maxSize > 0 && file.getSize() > maxSize) {
            return UploadResult.failed("The file size larger than " + maxSize);
        }
        return UploadResult.success();
    }

    /**
     * generate file name.
     * @param originalFilename originalFilename.
     * @return new file name.
     */
    public String generateFileName(String originalFilename) {
        return FileUtil.generateUUIDFileName(originalFilename);
    }

    public UploadFileProperties getProperties() {
        return properties;
    }


    protected UploadResponse buildResponse(UploadResult result, UploadMode.Mode mode) {
        return new UploadResponse() {
            @Override
            public UploadMode.Mode uploadMode() {
                return mode == null ? UploadMode.Mode.SYNC : mode;
            }
            @Override
            public UploadResult getResult(boolean syncWait) {
                return result;
            }
        };
    }




}
