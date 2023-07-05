import React from 'react'
import CatalogTile from "../catalog-tile/CatalogTile"

/**
 * renders a list of bundleGroup
 */
const CatalogTiles = ({bundleGroups, categoryDetails, onAfterSubmit, isVersionsPage, orgList, showFullPage, currentUserOrg}) => {
    const listItems = bundleGroups && bundleGroups.map((bundleGroup, index) =>{
        return <CatalogTile
        onAfterSubmit={onAfterSubmit} categoryDetails={categoryDetails} descriptionImage={bundleGroup.descriptionImage}
        key={index} bundleGroup={bundleGroup} isVersionsPage={isVersionsPage} orgList={orgList}
        showFullPage={showFullPage} {...bundleGroup}
        currentUserOrg={currentUserOrg}/>
    })

    return <div>{listItems}</div>
    }


export default CatalogTiles
