import React, { useEffect, useState } from "react";
import { Tag } from "carbon-components-react";
import { useHistory } from "react-router-dom";
import "./catalog-tile.scss";
import CatalogTileOverflowMenu from "./overflow-menu/CatalogTileOverflowMenu";
import { isHubUser } from "../../../helpers/helpers";
import { textFromStatus } from '../../../helpers/profiling';
import { BUNDLE_STATUS, HOME_TO_BG_PAGE_URL, VERSIONS_TO_BG_PAGE_URL } from "../../../helpers/constants";
import i18n from "../../../i18n";
import parse from 'html-react-parser';

const CatalogTile = ({
  bundleGroupId,
  name,
  organisationName,
  description,
  descriptionImage,
  categories,
  status,
  categoryDetails,
  onAfterSubmit,
  version,
  bundleGroup,
  isVersionsPage,
  orgList,
  showFullPage
}) => {
  const [categoryName, setCategoryName] = useState("")
  let bundleStatus = status

  useEffect(() => {

    if (categories) {
      const getCategoryNameById = (catId) => {
        return categoryDetails && categoryDetails.find(cat => cat.categoryId === catId);
      }
      const data = getCategoryNameById(categories[0]) && getCategoryNameById(categories[0]).name;
      setCategoryName(data);
    }

  }, [categories, categoryDetails])

  const history = useHistory()

  const handleClick = () => {
    if (isVersionsPage) {
      history.push(`${VERSIONS_TO_BG_PAGE_URL}${bundleGroup && bundleGroup.bundleGroupVersionId}`);
    } else {
      history.push(`${HOME_TO_BG_PAGE_URL}${bundleGroup && bundleGroup.bundleGroupVersionId}`);
    }
  }

  let tagColor
  switch (categoryName && categoryName.toLowerCase()) {
    case "component collection":
      tagColor = "red"
      break
    case "solution template":
      tagColor = "green"
      break
    case "PBC":
      tagColor = "blue"
      break
    default:
      tagColor = "blue"
  }

  //TODO refactor into an utility function
  return (
    <>
      <div className="CatalogTile">
        {isHubUser() && showFullPage && bundleStatus !== BUNDLE_STATUS.ARCHIVED && (
          <div className="CatalogTile-dropmenu">
            <CatalogTileOverflowMenu
              bundleGroupId={bundleGroupId}
              bundleStatus={bundleStatus}
              bundleName={name}
              onAfterSubmit={onAfterSubmit}
              bundleGroup={bundleGroup}
              isVersionsPage={isVersionsPage}
              orgList={orgList}
            />
          </div>
        )}
        <div onClick={handleClick} className="CatalogTile-card-wrapper">
          <div className="CatalogTile-card-icon">
            {descriptionImage ? (
              <img src={descriptionImage} alt="Logo" />
            ) : (
              <img
                src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/icon.svg`}
                alt="Logo"
              />
            )}
          </div>
          <div className="CatalogTile-card-title">{name}</div>
          <div className="CatalogTile-card-status">{organisationName}</div>
          {isHubUser() && showFullPage && (
            <div className="CatalogTile-card-status">
              {i18n.t(textFromStatus(status))}
            </div>
          )}
          <div className="CatalogTile-card-description">{parse(description)}</div>
          <div className="tag-setting">
            <Tag type={tagColor} title="Clear Filter">
              {categoryName}
            </Tag>
          </div>

          <div className="CatalogTile-card-status">{version}</div>
        </div>
      </div>
    </>
  )
}

export default CatalogTile
