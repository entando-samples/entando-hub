import {useEffect, useRef, useState} from "react"
import {Button, ButtonSet, TextInput, TextArea, Row, Column} from "carbon-components-react"
import {Table, TableHead, TableRow, TableHeader, TableBody, TableCell} from 'carbon-components-react';

import {Add16, Delete16, Edit16} from '@carbon/icons-react'

import "./bundles-of-bundle-group.scss"
import { bundleUrlSchema, bundleSrcUrlSchema} from "../../../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../../../helpers/validation/fillErrors";
import { BUNDLE_STATUS, GIT_REPO, BUNDLE_URL_REGEX, OPERATION, CHAR_LENGTH_255, CHAR_LIMIT_MSG_SHOW_TIME } from "../../../../../helpers/constants";
import i18n from "../../../../../i18n";
import {clickableUrlFromUri, setBundleNameFromRepoAddress} from "../../../../../helpers/helpers";
/*
BUNDLE:
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
} */

const BundleList = ({children = [], setGitSrcRepo, onDeleteBundle, disabled}) => {
    const [bundleSrcInvalid, setBundleSrcInvalid] = useState({})
    const [editBundleIndex, setEditBundleIndex] = useState()

    const onEditBundle = (index) => {
        setEditBundleIndex(index);
    }

    const onSrcBlur = () => {
        setEditBundleIndex(null);
    }

    const onSrcChange= (e, index) => {
        const value = e.target.value.trim();
        validateBundleSrcUrl(value, index);
        setGitSrcRepo(value, index)
    }

    const validateBundleSrcUrl = (value, index) => {
        ; (async () => {
            let validationError
            await bundleSrcUrlSchema.validate({gitSrcRepo: value}, {abortEarly: false}).catch(error => {
                validationError = fillErrors(error)
            })
            const invalidSet = {...bundleSrcInvalid}
            invalidSet[index] = (typeof validationError !== 'undefined');
            setBundleSrcInvalid(invalidSet);
        })()
    }

    let headers = [`${i18n.t('component.bundleModalFields.bundleUrl')}`, `${i18n.t('component.bundleModalFields.sourceUrl')}`];
    if (!disabled) {
        headers = [...headers, ''];
    }
    const rows = children;
    if (rows && rows.length) {
    return (
        <div className="BundlesOfBundleGroup-Bundle-list">
            <Table>
                <TableHead>
                    <TableRow>
                        {headers.map((header) => (
                            <TableHeader id={header.key} key={header}>
                                {header}
                            </TableHeader>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.map((row,index) => (
                        <TableRow key={'bundle'+index}>
                            <TableCell><a href={clickableUrlFromUri(row.gitRepoAddress)}
                                   target={"_blank"}  rel="noopener noreferrer">{row.gitRepoAddress}</a>
                            </TableCell>
                            <TableCell>
                                {(disabled && row.gitSrcRepoAddress) ? (
                                    <a href={row.gitSrcRepoAddress}
                                       target={"_blank"} rel="noopener noreferrer">{row.gitSrcRepoAddress}</a>
                                ) : (!disabled && !row.gitSrcRepoAddress && index !== editBundleIndex) ? (
                                    <Button iconDescription={`${i18n.t('component.bundleModalFields.addSourceUrl')}`}
                                            onClick={() => onEditBundle(index)}
                                            hasIconOnly renderIcon={Edit16} kind="secondary"/>
                                ) : !disabled && (
                                    <div onClick={() => onEditBundle(index)}>
                                        <TextArea value={row.gitSrcRepoAddress || ""}
                                                   disabled={disabled || (index !== editBundleIndex)}
                                                   onChange={(e) => onSrcChange(e,index)}
                                                   onBlur={onSrcBlur}
                                                   maxLength={CHAR_LENGTH_255}
                                                   invalid={bundleSrcInvalid && bundleSrcInvalid[index]}
                                                   invalidText={`${i18n.t('formValidationMsg.bundleSrcUrlFormat')}`}
                                                   autoComplete={"false"}
                                                   labelText={`${i18n.t('component.bundleModalFields.addSourceUrl')}`}
                                                   hideLabel={true}
                                                   rows={1}
                                        />
                                    </div>
                                )}
                            </TableCell>
                            {!disabled &&
                                <TableCell>
                                    <ButtonSet className={"BundlesOfBundleGroup-button-set"}>
                                        <Button disabled={disabled} iconDescription={`${i18n.t('component.button.delete')}`}
                                                onClick={() => onDeleteBundle(index)}
                                                hasIconOnly renderIcon={Delete16} kind="secondary"/>
                                    </ButtonSet>
                                </TableCell>
                            }
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    )} else {
        return null;
    }
}

const BundlesOfBundleGroup = ({
    onAddOrRemoveBundleFromList,
    initialBundleList,
    disabled = false,
    errorMsg,
    bundleStatus,
    mode,
    operation,
    bundleGroupIsEditable,
    displayContactUrl
}) => {

    useEffect(() => {
        setBundleList(initialBundleList)
    }, [initialBundleList])

    const [bundleList, setBundleList] = useState([])
    const [gitRepo, setGitRepo] = useState("")
    const [validationResult, setValidationResult] = useState({})
    const [isUrlReqValid, setIsUrlReqValid] = useState(false)
    const [isUrlBundleRexValid, setIsUrlBundleRexValid] = useState(false)
    const [showBundleUrlCharLimitErrMsg, setShowBundleUrlCharLimitErrMsg] = useState(false);
    const [mounted, setMounted] = useState(false);
    const timerRef = useRef(null);
    disabled = bundleGroupIsEditable && operation !== OPERATION.ADD_NEW_VERSION ? false : operation === OPERATION.ADD_NEW_VERSION ? false : disabled

    useEffect(() => {
        !bundleList.length && setIsUrlReqValid(false);
    }, [bundleList])

    const onChangeHandler = (e) => {
        const value = e.target.value.trim()
        value.length > 0 ? setIsUrlReqValid(true) : setIsUrlReqValid(false)
        setGitRepo(value)
        validateBundleUrl(e)
    }

    const setGitSrcRepo = (value, index) => {
        const newBundleList = [...bundleList]
        if (index > (newBundleList.length - 1)) {
            console.warn("Illegal index", index)
            return
        }
        newBundleList[index].gitSrcRepoAddress = value;
        setBundleList(newBundleList);
    }

    const validateBundleUrl = (e) => {
        const value = e.target.value
        value.trim().length > 0 && new RegExp(BUNDLE_URL_REGEX).test(value) ? setIsUrlBundleRexValid(true) : setIsUrlBundleRexValid(false)
            ; (async () => {
                let validationError
                await bundleUrlSchema.validate({ gitRepo: value }, { abortEarly: false }).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    value.trim().length === 0 && delete validationError.gitRepo;
                    setValidationResult(validationError)
                } else {
                  setValidationResult({});
                }
            })()
    }

    const onAddBundle = (e) => {
        (async () => {
            //validation

            let validationError
            await bundleUrlSchema.validate({gitRepo}, {abortEarly: false}).catch(error => {
                validationError = fillErrors(error)
            })
            if (validationError) {
                setValidationResult(validationError)
                return //don't send the form
            }

            setValidationResult({})

            if (gitRepo === "") {
                return
            }
            let newBundleList = [...bundleList, {
                name: setBundleNameFromRepoAddress({gitRepoAddress: gitRepo}).name,
                description: gitRepo,
                gitRepoAddress: gitRepo,
                dependencies: [],
                bundleGroups: []
            }]
            setBundleList(newBundleList)
            onAddOrRemoveBundleFromList(newBundleList)
            setGitRepo("")
            setIsUrlBundleRexValid(false)
        })()
    }

    const onDeleteBundle = (index) => {
        if ((index > -1) && (index < bundleList.length)) {
            bundleList.splice(index, 1)
        }
        setBundleList(bundleList)
        onAddOrRemoveBundleFromList(bundleList)
    }

    const textInputProps = {
        id: "bundle",
        labelText: (bundleStatus === BUNDLE_STATUS.PUBLISH_REQ || bundleStatus === BUNDLE_STATUS.PUBLISHED) && !displayContactUrl ?
            `${i18n.t('component.bundleModalFields.addBundleUrl')} *` : `${i18n.t('component.bundleModalFields.addBundleUrl')}`
    }

    let bundleUrlErrorResult = "";
    let minOneBundle = `${i18n.t('formValidationMsg.atleastOneUrl')}`
    if (
        !displayContactUrl &&
        !initialBundleList.length &&
        mode === 'Edit' &&
        (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)
    ) {
        /**
         * Show BUNDLE_URL_REGEX_FAIL Msg when Mode is Edit and BUNDLE_STATUS is
         * PUBLISHED OR BUNDLE_STATUS.PUBLISH_REQ, Otherwise show minOneBundle
         */
        bundleUrlErrorResult = (validationResult && validationResult.gitRepo && validationResult.gitRepo.length) ? `${i18n.t('formValidationMsg.bundleUrlFormat')}` : minOneBundle
    } else if (
        !displayContactUrl &&
        errorMsg === minOneBundle &&
        Object.keys(validationResult).length === 0 &&
        initialBundleList.length < 1
        && (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)
    ) {
            bundleUrlErrorResult = minOneBundle;
    } else if (!isUrlBundleRexValid) {
      bundleUrlErrorResult = validationResult["gitRepo"] &&
        validationResult["gitRepo"].join("; ")
    } else {
      bundleUrlErrorResult = null;
    }
  
    if(showBundleUrlCharLimitErrMsg) {
        bundleUrlErrorResult = i18n.t('formValidationMsg.maxBundleUrl255Char');
    }

    /**
     * Handle keyPress event for input field and show/hide character limit error message
     * @param {*} e
     */
     const keyPressHandler = (e) => {
        if (e.target.value.length >= CHAR_LENGTH_255) {
            bundleUrlErrorResult = i18n.t('formValidationMsg.maxBundleUrl255Char')
            setShowBundleUrlCharLimitErrMsg(true);
            timerRef.current = setTimeout(disappearCharLimitErrMsg, CHAR_LIMIT_MSG_SHOW_TIME);
        }
    }

    const disappearCharLimitErrMsg = () => {
        if (mounted) {
            bundleUrlErrorResult = "";
            setShowBundleUrlCharLimitErrMsg(false);
        }
    }

    useEffect(() => {
        setMounted(true);
        // Clear the interval when the component unmounts
        return () => {
            setMounted(false);
            clearTimeout(timerRef.current);
        };
    }, []);

    let invalid = (!isUrlReqValid && (!!validationResult[GIT_REPO] || !!bundleUrlErrorResult))
        || (bundleList.length === 0 && !!bundleUrlErrorResult) || (!isUrlBundleRexValid ? !!validationResult[GIT_REPO] : showBundleUrlCharLimitErrMsg);

    return (
        <>
            <Row>
                {!disabled &&
                <Column sm={16} md={8} lg={8}>
                    <TextInput value={gitRepo}
                               disabled={disabled}
                               onChange={onChangeHandler}
                               {...textInputProps}
                               maxLength={CHAR_LENGTH_255}
                               invalid={invalid}
                               invalidText={bundleUrlErrorResult}
                               autoComplete={"false"}
                               onKeyPress={keyPressHandler}
                    />
                </Column>
                }
                {!disabled &&
                <Column sm={16} md={8} lg={8}>
                    <div className="BundlesOfBundleGroup-add-button">
                        <Button disabled={disabled} onClick={onAddBundle}
                                renderIcon={Add16}>
                            {i18n.t('component.button.add')}
                        </Button>
                    </div>
                </Column>
                }
                <Column sm={16} md={16} lg={16}>
                    <div>
                        <BundleList children={bundleList}
                            onDeleteBundle={onDeleteBundle}
                            setGitSrcRepo={setGitSrcRepo}
                            disabled={disabled} />
                    </div>
                </Column>
            </Row>
        </>
    )

}

export default BundlesOfBundleGroup


