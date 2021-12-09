import {useEffect, useState} from "react"
import {Button, Tag, TextInput, Row, Column} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'

import "./bundles-of-bundle-group.scss"
import {
    bundleOfBundleGroupSchema,
} from "../../../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../../../helpers/validation/fillErrors";
import { BUNDLE_STATUS, GIT_REPO, BUNDLE_URL_REGEX, BUNDLE_URL_REGEX_FAIL } from "../../../../../helpers/constants";

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

const parseGitRepoAddr = (gitRepoAddress) => {
    return gitRepoAddress ? {
        name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1,
            gitRepoAddress.lastIndexOf(".")),
        gitRepoAddress
    } : {
        name: "",
        gitRepoAddress: ""
    }
}

const BundleList = ({children = [], onDeleteBundle, disabled}) => {
    const elemList = children.map(bundle => bundle.gitRepoAddress).map(
        parseGitRepoAddr).map((childrenInfo, index) =>
        <li key={index.toString()}>
            <Tag disabled={disabled}>
                {disabled && childrenInfo.name}
                {!disabled && <a href={childrenInfo.gitRepoAddress}
                   target={"_new"}>{childrenInfo.name}</a>}
                {!disabled && <span
                    className="button-delete"
                    onClick={() => onDeleteBundle(childrenInfo.gitRepoAddress)}>
                +
              </span>}
            </Tag>
        </li>
    )

    return (
        <div className="BundlesOfBundleGroup-Bundle-list">
            {/*List of Bundles*/}
            <ul className="BundlesOfBundleGroup-Bundle-list-ul">
                {elemList}
            </ul>
        </div>
    )
}

const MIN_ONE_BUNDLE_ERROR = "Please add at least one bundle before publishing this bundle group.";

const BundlesOfBundleGroup = ({
    onAddOrRemoveBundleFromList,
    initialBundleList,
    disabled = false,
    minOneBundleError,
    bundleStatus,
    mode
}) => {

    useEffect(() => {
        setBundleList(initialBundleList)
    }, [initialBundleList])

    const [bundleList, setBundleList] = useState([])
    const [gitRepo, setGitRepo] = useState("")
    const [validationResult, setValidationResult] = useState({})
    const [isUrlReqValid, setIsUrlReqValid] = useState(false);
    const [isUrlBundleRexValid, setIsUrlBundleRexValid] = useState(false);

    useEffect(() => {
        !bundleList.length && setIsUrlReqValid(false);
    }, [bundleList])

    const onChangeHandler = (e) => {
        const value = e.target.value
        value.trim().length > 0 ? setIsUrlReqValid(true) : setIsUrlReqValid(false)
        setGitRepo(value)
    }

    const onBlurHandler = (e) => {
        const value = e.target.value
        value.trim().length > 0 && new RegExp(BUNDLE_URL_REGEX).test(value) ? setIsUrlBundleRexValid(true) : setIsUrlBundleRexValid(false)
    }

    const onAddBundle = (e) => {
        (async () => {
            //validation

            let validationError
            await bundleOfBundleGroupSchema.validate({gitRepo}, {abortEarly: false}).catch(error => {
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
                name: parseGitRepoAddr(gitRepo).name,
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

    const onDeleteBundle = (gitRepoAddress) => {
        const newBundleList = bundleList.filter(
            bundle => bundle.gitRepoAddress !== gitRepoAddress)
        setBundleList(newBundleList)
        onAddOrRemoveBundleFromList(newBundleList)
    }

    const textInputProps = {
        id: "bundle",
        labelText: bundleStatus === BUNDLE_STATUS.PUBLISH_REQ || bundleStatus === BUNDLE_STATUS.PUBLISHED ? 'Add Url Bundle *' : 'Add Url Bundle'
    }

    let bundleUrlErrorResult = "";


    if (!initialBundleList.length && mode === 'Edit' && (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)) {
        // Show BUNDLE_URL_REGEX_FAIL Msg when Mode is Edit and BUNDLE_STATUS is PUBLISHED OR BUNDLE_STATUS.PUBLISH_REQ, Otherwise show MIN_ONE_BUNDLE_ERROR
        bundleUrlErrorResult = (validationResult && validationResult.gitRepo && validationResult.gitRepo.length) ? BUNDLE_URL_REGEX_FAIL : MIN_ONE_BUNDLE_ERROR
    } else if (minOneBundleError === MIN_ONE_BUNDLE_ERROR &&
        Object.keys(validationResult).length === 0 &&
        initialBundleList.length < 1 && (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)) {
        bundleUrlErrorResult = MIN_ONE_BUNDLE_ERROR;
    } else {
        if (!isUrlBundleRexValid) {
            bundleUrlErrorResult = validationResult["gitRepo"] &&
                validationResult["gitRepo"].join("; ")
        } else {
            bundleUrlErrorResult = null;
        }
    }
    return (
        <>
            <Row>
                <Column sm={16} md={8} lg={8}>
                    <TextInput value={gitRepo}
                               disabled={disabled}
                               onChange={onChangeHandler} {...textInputProps}
                               invalid={!isUrlReqValid ? (!!validationResult[GIT_REPO] || !!bundleUrlErrorResult) : (!isUrlBundleRexValid ? !!validationResult[GIT_REPO] : null)}
                               invalidText={bundleUrlErrorResult}
                               onBlur={onBlurHandler}
                    />
                </Column>
                <Column sm={16} md={8} lg={8}>
                    <div className="BundlesOfBundleGroup-add-button">
                        <Button disabled={disabled} onClick={onAddBundle}
                                renderIcon={Add16}>
                            Add
                        </Button>
                    </div>
                </Column>
                <Column sm={16} md={16} lg={16}>
                    <div>
                        <BundleList children={bundleList}
                                    onDeleteBundle={onDeleteBundle}
                                    disabled={disabled}/>
                    </div>
                </Column>
            </Row>
        </>
    )

}

export default BundlesOfBundleGroup


